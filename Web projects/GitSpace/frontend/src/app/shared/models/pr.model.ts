export interface PullRequest {
  id: number;
  prNumber: number;
  title: string;
  description: string;
  fromBranch: string;
  toBranch: string;
  authorUsername: string;
  state: 'OPEN' | 'MERGED' | 'CLOSED';
  hasConflicts: boolean;
  createdAt: string;
}

export interface PullRequestFile {
  changeType: string; // ADDED, MODIFIED, DELETED
  filePath: string;
  diff: string; // Raw diff content
}