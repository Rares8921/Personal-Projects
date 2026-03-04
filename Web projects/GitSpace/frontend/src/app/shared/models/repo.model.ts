export interface Repository {
  id: number;
  name: string;
  description?: string;
  isPublic: boolean;      
  defaultBranch: string;
  starsCount: number;      
  forksCount: number;     
  ownerUsername: string;
  cloneUrlHttp?: string;
  createdAt?: Date | string;
  updatedAt: Date | string;
  isArchived: boolean;
  language?: string; 
}