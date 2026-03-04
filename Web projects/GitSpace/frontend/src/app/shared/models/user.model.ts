import { Repository } from "./repo.model";

export interface User {
  id: number;
  username: string;
  email?: string;
  fullName?: string;
  avatarUrl?: string;
  bio?: string;
  token?: string; // JWT
  location?: string;
  website?: string;
  roles?: string[];
}

export interface UserSettingsDto {
  fullName?: string;
  bio?: string;
  location?: string;
  website?: string;
  isPublic?: boolean;
}

export interface UserProfile {
  followers: number;
  following: number;
  location?: string;
  website?: string;
  createdAt: string;
}

// Corespunde cu main.backend.dto.user.UserProfileDto
export interface UserProfileDto {
  id: number;
  username: string;
  fullName: string;
  avatarUrl: string;
  bio: string;
  location: string;
  website: string;
  createdAt: string;
  followers: number;
  following: number;
  repos: Repository[]; 
  isPublic: boolean;
  isFollowing: boolean;
  isPending: boolean;
}
