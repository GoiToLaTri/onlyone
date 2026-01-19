import {
  Controller,
  Get,
  Inject,
  OnModuleInit,
  Query,
  UseGuards,
} from '@nestjs/common';
import {
  GrpcUserResponse,
  HttpUserResponse,
  UserRequest,
  UserServiceClient,
} from './interfaces';
import { ClientGrpc } from '@nestjs/microservices';
import { map, Observable } from 'rxjs';
import { Metadata } from '@grpc/grpc-js';
import { AuthGuard } from 'src/guards/auth.guard';
import { requestContext } from 'src/common/contexts/request-context';
import { timestampToDate } from 'src/utils';

@UseGuards(AuthGuard)
@Controller()
export class UserController implements OnModuleInit {
  private userServiceClient: UserServiceClient;
  constructor(@Inject('USER_PACKAGE') private client: ClientGrpc) {}

  onModuleInit() {
    this.userServiceClient =
      this.client.getService<UserServiceClient>('UserService');
  }

  @Get('info')
  findByPublicId(@Query('id') id: string): Observable<GrpcUserResponse> {
    const store = requestContext.getStore();
    const token = store?.token;
    const metadata = new Metadata();
    metadata.add('authorization', `Bearer ${token}`);

    const request: UserRequest = { publicId: id };

    return this.userServiceClient.findByPublicId(request, metadata);
  }

  @Get('profile')
  profile(): Observable<HttpUserResponse> {
    const store = requestContext.getStore();
    const token = store?.token;
    const metadata = new Metadata();
    metadata.add('authorization', `Bearer ${token}`);

    return this.userServiceClient.profile(undefined, metadata).pipe(
      map(
        (res): HttpUserResponse => ({
          ...res,
          createdAt: res.createdAt
            ? timestampToDate(res.createdAt).toISOString()
            : null,
        }),
      ),
    );
  }
}
