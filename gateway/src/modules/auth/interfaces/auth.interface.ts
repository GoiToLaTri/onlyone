import { Metadata } from '@grpc/grpc-js';
import { Observable } from 'rxjs';

export interface AuthServiceClient {
  introspect(
    request: IntrospecRequest,
    metadata?: Metadata,
  ): Observable<IntrospecResponse>;
}

export interface IntrospecRequest {
  token: string;
}

export interface IntrospecResponse {
  token: string;
}
