package main.protocol;

import main.domain.Reference;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Handles communication with a remote Git server over HTTP (Smart HTTP Protocol).
 * This class implements the client-side of the "git-upload-pack" service.
 */
public class HttpGitTransport implements GitTransport {

    private static final String SERVICE_UPLOAD_PACK = "git-upload-pack";
    private static final MediaType MEDIA_TYPE_REQUEST = MediaType.parse("application/x-git-upload-pack-request");

    private static final String SERVICE_RECEIVE_PACK = "git-receive-pack";
    private static final MediaType MEDIA_TYPE_RECEIVE_REQUEST = MediaType.parse("application/x-git-receive-pack-request"); // NOU

    // Limits
    private static final int HASH_LENGTH = 40;

    /**
     * The HTTP client used to make requests.
     * Configured with timeouts to avoid hanging indefinitely.
     */
    private final OkHttpClient httpClient;

    private final URI baseUri;

    private final String token;

    public HttpGitTransport(URI baseUri, String token) {
        this.baseUri = Objects.requireNonNull(baseUri, "Base URI cannot be null");
        this.token = token;

        if (!baseUri.getScheme().startsWith("http")) {
            throw new IllegalArgumentException("GitClient currently only supports 'http' and 'https' protocols. URI: " + baseUri);
        }

        this.httpClient = new OkHttpClient.Builder()
                .connectTimeout(Duration.ofSeconds(10))
                .readTimeout(Duration.ofSeconds(30))
                .writeTimeout(Duration.ofSeconds(30))
                .followRedirects(true)
                .addNetworkInterceptor(chain -> {
                    Request original = chain.request();
                    Request.Builder builder = original.newBuilder()
                            .header("User-Agent", "git-core-lib/1.0");

                    // Daca avem un token, il punem in header
                    if (this.token != null && !this.token.isBlank()) {
                        builder.header("Authorization", "Bearer " + this.token);
                    }

                    return chain.proceed(builder.build());
                })
                .build();
    }

    /**
     * Fetches the list of available references (branches, tags) from the remote.
     * Performs the "info/refs" discovery request.
     */
    @Override
    public List<Reference> fetchReferences() throws IOException {
        return fetchRefsInternal(SERVICE_UPLOAD_PACK);
    }

    /**
     * Requests a "packfile" from the remote.
     * POSTs the "want" list to /git-upload-pack.
     */
    @Override
    public byte[] getPack(Reference reference) throws IOException {
        Objects.requireNonNull(reference);

        // Protocol: "want <hash>", "flush", "done"
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();

        PacketLine.data("want " + reference.hash() + "\n").serialize(buffer);
        PacketLine.flush().serialize(buffer);
        PacketLine.data("done\n").serialize(buffer);

        byte[] requestBodyBytes = buffer.toByteArray();

        HttpUrl url = Objects.requireNonNull(HttpUrl.get(baseUri)).newBuilder()
                .addPathSegment(SERVICE_UPLOAD_PACK)
                .build();

        Request request = new Request.Builder()
                .url(url)
                .post(RequestBody.create(requestBodyBytes, MEDIA_TYPE_REQUEST))
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            validateResponse(response);

            try (ResponseBody body = response.body()) {
                if (body == null) throw new IOException("Empty response body from git-upload-pack");

                InputStream in = body.byteStream();

                consumeServerNegotiation(in);

                return in.readAllBytes();
            }
        }
    }

    private List<Reference> fetchRefsInternal(String serviceName) throws IOException {
        HttpUrl url = Objects.requireNonNull(HttpUrl.get(baseUri)).newBuilder()
                .addPathSegment("info")
                .addPathSegment("refs")
                .addQueryParameter("service", serviceName)
                .build();

        Request request = new Request.Builder().url(url).get().build();

        try (Response response = httpClient.newCall(request).execute()) {
            validateResponse(response);

            try (ResponseBody body = response.body()) {
                if (body == null) throw new IOException("Empty response body from info/refs");
                return new PacketLineReader(body.byteStream()).readAdvertisedRefs();
            }
        }
    }

    @Override
    public void push(String remoteUrl, String commitHash, String refName, byte[] packData) throws IOException {
        fetchRefsInternal(SERVICE_RECEIVE_PACK);

        String zeroId = "0".repeat(40);

        String command = String.format("%s %s refs/heads/%s\0 report-status\n", zeroId, commitHash, refName);

        ByteArrayOutputStream bodyStream = new ByteArrayOutputStream();

        PacketLine.data(command).serialize(bodyStream);
        PacketLine.flush().serialize(bodyStream);

        bodyStream.write(packData);

        HttpUrl url = Objects.requireNonNull(HttpUrl.get(baseUri)).newBuilder()
                .addPathSegment(SERVICE_RECEIVE_PACK)
                .build();

        Request request = new Request.Builder()
                .url(url)
                .post(RequestBody.create(bodyStream.toByteArray(), MEDIA_TYPE_RECEIVE_REQUEST))
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            validateResponse(response);
        }
    }

    @Override
    public void close() {
        httpClient.dispatcher().executorService().shutdown();
        httpClient.connectionPool().evictAll();
        if (httpClient.cache() != null) {
            try { httpClient.cache().close(); } catch (IOException e) { /* ignore */ }
        }
    }

    /**
     * Consumes the negotiation PacketLines from the server input stream.
     * The server responds to "done" with a series of ACKs (if common commits exist)
     * and finally a NAK (indicating end of negotiation), OR just a NAK.
     * After this method returns, the InputStream is positioned at the start of the Packfile.
     *
     * @throws IOException If the negotiation fails or protocol is violated.
     */
    private void consumeServerNegotiation(InputStream in) throws IOException {
        int maxNegotiationLines = 1000;
        int linesRead = 0;

        while (linesRead < maxNegotiationLines) {
            linesRead++;

            PacketLine line = parsePacketLine(in);

            if (line == null) {
                throw new IOException("Unexpected EOF during protocol negotiation. Server cut connection.");
            }

            if (line instanceof PacketLine.Flush) {
                continue;
            }

            if (line instanceof PacketLine.Data data) {
                String content = new String(data.content(), StandardCharsets.US_ASCII);

                // Case 1: NAK - The standard "I'm ready to send data" signal.
                if (content.startsWith("NAK")) {
                    return; // Negotiation complete. Packfile follows.
                }

                // Case 2: ACK - Server acknowledges objects we have.
                // Format: "ACK <hash> continue" or "ACK <hash> ready" or just "ACK <hash>"
                if (content.startsWith("ACK")) {
                    if (content.contains("ready")) {
                        return;
                    }
                    // it's an intermediate ACK, keep looping until NAK.
                    continue;
                }


                if (content.startsWith("ERR")) {
                    throw new IOException("Remote Git Server Error: " + content);
                }

                throw new IOException("Protocol error: Expected ACK/NAK but got: " + content);
            }
        }

        throw new IOException("Negotiation loop exceeded limit. Server spamming ACKs?");
    }


    private List<Reference> parseRefsFromStream(InputStream in) throws IOException {
        List<Reference> refs = new ArrayList<>();
        List<PacketLine> lines = parsePacketLines(in);

        for (PacketLine line : lines) {
            if (line instanceof PacketLine.Data data) {
                if (data.isComment()) continue; // Skip comments like "# service=..."

                String content = new String(data.content(), StandardCharsets.UTF_8).trim();

                // Format: <hash> <refName>[\0capabilities]
                int spaceIndex = content.indexOf(' ');
                if (spaceIndex == -1) continue;

                String hash = content.substring(0, spaceIndex);
                String rest = content.substring(spaceIndex + 1);

                // Handle capabilities separator (null byte) if present
                int nullIndex = rest.indexOf('\0');
                String name = (nullIndex != -1) ? rest.substring(0, nullIndex) : rest;

                if (hash.length() == HASH_LENGTH) {
                    refs.add(new Reference(name, hash));
                }
            }
        }
        return refs;
    }

    public List<PacketLine> parsePacketLines(InputStream in) throws IOException {
        List<PacketLine> lines = new ArrayList<>();
        PacketLine line;
        while ((line = parsePacketLine(in)) != null) {
            lines.add(line);
        }
        return lines;
    }

    public PacketLine parsePacketLine(InputStream in) throws IOException {
        byte[] lenBytes = new byte[4];
        // Read exactly 4 bytes for length
        int bytesRead = in.readNBytes(lenBytes, 0, 4);
        if (bytesRead < 4) return null; // EOF

        String lenStr = new String(lenBytes, StandardCharsets.US_ASCII);
        int len;
        try {
            len = Integer.parseInt(lenStr, 16);
        } catch (NumberFormatException e) {
            throw new IOException("Invalid packet length: " + lenStr);
        }

        if (len == 0) return PacketLine.flush();

        // Length includes the 4 bytes of the prefix, so payload is len - 4
        int payloadLen = len - 4;
        if (payloadLen < 0) throw new IOException("Negative payload length implied: " + payloadLen);
        if (payloadLen == 0) return PacketLine.data(new byte[0]); // Empty packet

        byte[] payload = in.readNBytes(payloadLen);
        if (payload.length != payloadLen) {
            throw new IOException("Unexpected EOF while reading packet payload");
        }

        return PacketLine.data(payload);
    }

    private void validateResponse(Response response) throws IOException {
        if (!response.isSuccessful()) {
            throw new IOException("Git request failed: HTTP " + response.code() + " " + response.message());
        }
    }

}