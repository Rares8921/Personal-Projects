export interface Issue {
  id: number;
  number: number;
  title: string;
  body?: string;
  authorUsername: string;
  createdAt: string; // ISO Date string
  isOpen: boolean;
  commentsCount?: number;
}

export interface IssueComment {
  id: number;
  body: string;
  authorUsername: string;
  authorAvatarUrl?: string;
  createdAt: string;
}