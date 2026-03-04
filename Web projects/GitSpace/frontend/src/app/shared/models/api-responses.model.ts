export interface JwtAuthResponse {
  token: string;
  username: string;
}

export interface FileUploadResponse {
  fileName: string;
  fileDownloadUri: string;
}