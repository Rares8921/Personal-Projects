export interface Activity {
  id: number;
  type: string; // 'PUSH', 'ISSUE_CREATED'
  description: string;
  actorUsername: string;
  repoName: string;
  timestamp: string; // ISO Date string
}