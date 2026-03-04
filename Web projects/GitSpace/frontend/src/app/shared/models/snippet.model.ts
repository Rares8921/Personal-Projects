export interface Snippet {
  id: number;
  description: string;
  filename: string;
  content: string;
  language: string;
  authorUsername: string;
  createdAt: string;
  updatedAt?: string;
}