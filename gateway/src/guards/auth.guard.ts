import { Metadata } from '@grpc/grpc-js';
import {
  CanActivate,
  ExecutionContext,
  Inject,
  Injectable,
} from '@nestjs/common';
import { ClientGrpc } from '@nestjs/microservices';
import { Request } from 'express';
import { catchError, map, Observable, throwError } from 'rxjs';
import { requestContext } from 'src/common/contexts/request-context';
import { GrpcError } from 'src/common/filters/grpc-error.interface';

import {
  AuthServiceClient,
  IntrospecRequest,
} from 'src/modules/auth/interfaces';

@Injectable()
export class AuthGuard implements CanActivate {
  private authServiceClient: AuthServiceClient;

  constructor(@Inject('AUTHENTICATION_PACKAGE') private client: ClientGrpc) {}

  onModuleInit() {
    this.authServiceClient = this.client.getService<AuthServiceClient>(
      'AuthenticationService',
    );
  }

  canActivate(
    context: ExecutionContext,
  ): boolean | Promise<boolean> | Observable<boolean> {
    const request: Request = context.switchToHttp().getRequest<Request>();
    const token = request.headers['authorization'];
    const platform = request.headers['sec-ch-ua-platform'];
    const platformVersion = request.headers['sec-ch-ua-platform-version'];
    const arch = request.headers['sec-ch-ua-arch'];
    const userAgent = request.headers['user-agent'];

    const metadata: Metadata = new Metadata();
    metadata.add('ua-platform', JSON.stringify(platform));
    metadata.add('ua-platform-version', JSON.stringify(platformVersion));
    metadata.add('ua-arch', JSON.stringify(arch));
    metadata.add('user-agent', JSON.stringify(userAgent));
    console.log(':::: token', token);
    if (!token) return false;
    metadata.add('Authorization', token);
    const introspectRequest: IntrospecRequest = { token };

    return this.authServiceClient.introspect(introspectRequest, metadata).pipe(
      map((response) => {
        const store = requestContext.getStore();
        if (store) store.token = response.token;
        return true;
      }),
      catchError((err: GrpcError) => throwError(() => err)),
    );
  }
}

/**
 * 'sec-ch-ua-platform': 'Android',
  'sec-ch-ua-platform-version': '10',
  'sec-ch-ua-arch': 'arm',
  authorization: 'Bearer kl7g3GUbpnVFtLKbUBIAeGLo-8RZcMdSaEWmW0cY0Io',
  'user-agent': 'PostmanRuntime/7.51.0',
 */
