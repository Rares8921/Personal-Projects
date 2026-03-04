package main.protocol;

import main.domain.Reference;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class SshGitTransport implements GitTransport {

    private final String user;
    private final String host;
    private final int port;
    private final String path;

    public SshGitTransport(URI uri) {
        this.host = uri.getHost();
        this.port = uri.getPort() == -1 ? 22 : uri.getPort();
        this.path = uri.getPath();
        String userInfo = uri.getUserInfo();
        this.user = (userInfo != null) ? userInfo : System.getProperty("user.name");
    }

    @Override
    public List<Reference> fetchReferences() throws IOException {
        Process process = startSshProcess("git-upload-pack");
        try (InputStream stdout = process.getInputStream()) {

            PacketLineReader reader = new PacketLineReader(stdout);
            return reader.readAdvertisedRefs();

        } finally {
            cleanup(process);
        }
    }

    @Override
    public byte[] getPack(Reference reference) throws IOException {
        Process process = startSshProcess("git-upload-pack");

        try (InputStream stdout = process.getInputStream();
             OutputStream stdin = process.getOutputStream();
             InputStream stderr = process.getErrorStream()) {

            PacketLineReader reader = new PacketLineReader(stdout);

            reader.readAdvertisedRefs(); // Return value ignored, just consuming buffer.

            // want request
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            PacketLine.data("want " + reference.hash() + "\n").serialize(buffer);
            PacketLine.flush().serialize(buffer);
            PacketLine.data("done\n").serialize(buffer);

            stdin.write(buffer.toByteArray());
            stdin.flush();

            if (stderr.available() > 0) {
                String error = new String(stderr.readAllBytes(), StandardCharsets.UTF_8);
                if (!error.isBlank()) throw new IOException("SSH Error: " + error);
            }

            // ACK/NAK
            reader.consumeNegotiation();

            return stdout.readAllBytes();

        } finally {
            cleanup(process);
        }
    }

    @Override
    public void push(String remoteUrl, String commitHash, String refName, byte[] packData) throws IOException {
        // Push foloseste receive-pack
        Process process = startSshProcess("git-receive-pack");

        try (InputStream stdout = process.getInputStream();
             OutputStream stdin = process.getOutputStream();
             InputStream stderr = process.getErrorStream()) {

            PacketLineReader reader = new PacketLineReader(stdout);
            reader.readAdvertisedRefs();

            // Format: old-hash new-hash refname
            // Pentru creare branch nou/initial push, old-hash este zero.
            String zeroId = "0".repeat(40);
            String command = String.format("%s %s refs/heads/%s\0report-status\n", zeroId, commitHash, refName);

            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            PacketLine.data(command).serialize(buffer);
            PacketLine.flush().serialize(buffer);

            stdin.write(buffer.toByteArray());

            stdin.write(packData);
            stdin.flush();

            checkStderr(stderr);

            // read status (Optional: check for "unpack ok")
            stdout.readAllBytes();

        } finally {
            cleanup(process);
        }
    }

    private Process startSshProcess(String commandName) throws IOException {
        List<String> cmd = new ArrayList<>();
        cmd.add("ssh");
        cmd.add("-p");
        cmd.add(String.valueOf(port));
        cmd.add("-o");
        cmd.add("BatchMode=yes");
        cmd.add(user + "@" + host);
        cmd.add("git-upload-pack '" + path + "'");

        return new ProcessBuilder(cmd).start();
    }

    private void checkStderr(InputStream stderr) throws IOException {
        if (stderr.available() > 0) {
            String error = new String(stderr.readAllBytes(), StandardCharsets.UTF_8);
            if (!error.isBlank() && (error.contains("fatal") || error.contains("error"))) {
                throw new IOException("SSH Error: " + error);
            }
        }
    }

    private void cleanup(Process process) {
        if (process.isAlive()) {
            process.destroy();
            try { process.waitFor(1, TimeUnit.SECONDS); } catch (InterruptedException e) {}
            if (process.isAlive()) process.destroyForcibly();
        }
    }
}