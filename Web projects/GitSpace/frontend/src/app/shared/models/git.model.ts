export interface GitTreeEntry {
  path: string; // Full path
  name: string; // Filename
  type: 'blob' | 'tree';
  mode: string;
  size: number;
  sha: string;
}

export interface GitCommit {
  hash: string;
  shortHash: string;
  message: string;
  authorName: string;
  authorEmail: string;
  date: string; // ISO Date string
}

export interface GitBlob {
  content: string; // Base64 encoded content
  size: number;
}