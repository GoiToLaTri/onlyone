import { Module } from '@nestjs/common';
import { UserController } from './user.controller';
import { UserService } from './user.service';
import { ClientsModule, Transport } from '@nestjs/microservices';
import { join } from 'path';
import { appConstantConfig } from 'src/common/configs/appconstant.config';

@Module({
  imports: [
    ClientsModule.register([
      {
        name: 'USER_PACKAGE',
        transport: Transport.GRPC,
        options: {
          package: 'user',
          protoPath: join(
            __dirname,
            '..',
            '..',
            'proto/user/user-service.proto',
          ),
          url: appConstantConfig.getGrpcUserServiceUri(),
        },
      },
    ]),
  ],
  controllers: [UserController],
  providers: [UserService],
})
export class UserModule {}
