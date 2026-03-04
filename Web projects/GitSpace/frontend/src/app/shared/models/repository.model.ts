export interface Repository {
  id: number;
  name: string;
  description: string;
  language: string;
  stars: number;
  forks: number;
  updatedAt: Date;
  isPublic: boolean;
}