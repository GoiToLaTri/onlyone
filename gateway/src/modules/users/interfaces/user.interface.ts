import { Metadata } from '@grpc/grpc-js';
import { Observable } from 'rxjs';
import { GrpcTimestamp } from 'src/utils';

export interface UserServiceClient {
  findByPublicId(
    request: UserRequest,
    metadata?: Metadata,
  ): Observable<GrpcUserResponse>;
  profile(
    request: undefined,
    metadata?: Metadata,
  ): Observable<GrpcUserResponse>;
}

export interface UserRequest {
  publicId: string;
}

export interface GrpcUserResponse {
  publicId: string;
  name: string;
  email: string;
  createdAt: GrpcTimestamp;
}

export interface HttpUserResponse {
  publicId: string;
  name: string;
  email: string;
  createdAt: string | null;
}
