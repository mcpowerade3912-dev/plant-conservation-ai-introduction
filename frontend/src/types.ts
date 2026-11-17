export type Role = 'ADMIN' | 'REVIEWER' | 'USER';

export interface User {
  id: number;
  username: string;
  nickname: string;
  phone: string;
  email: string;
  role: Role;
  status: number;
  createTime?: string;
  updateTime?: string;
}

export interface Intro {
  id: number;
  userId: number;
  creatorName: string;
  plantName: string;
  scientificName: string;
  origin: string;
  introductionDate: string;
  quantity: number;
  description: string;
  imageUrl?: string;
  status: 'PENDING' | 'APPROVED' | 'REJECTED';
  createTime?: string;
  updateTime?: string;
}

export interface Plant {
  id: number;
  name: string;
  scientificName: string;
  category: string;
  origin: string;
  quantity: number;
  status: string;
  imageUrl?: string;
  introRecordId?: number;
  createTime?: string;
  updateTime?: string;
}

export interface PlantSpeciesCandidate {
  scientificName: string;
  family: string;
  genus: string;
  origin: string;
  commonNames: string[];
  confidence: number;
  note: string;
}

export interface IntroductionAssistResult {
  candidates: PlantSpeciesCandidate[];
  careTips: string[];
  note: string;
}

export interface SystemSettings {
  systemName: string;
  supportContact: string;
  defaultPlantCategory: string;
  uploadMaxSizeMb: number;
  aiEnabled: boolean;
  aiBaseUrl: string;
  aiModel: string;
  aiTimeoutSeconds: number;
  aiRateLimitPerMinute: number;
  aiApiKeyConfigured: boolean;
  updateTime?: string;
}
