import { Global, Module } from '@nestjs/common';
import { ClientsModule, Transport } from '@nestjs/microservices';
import { join } from 'path';
import { appConstantConfig } from 'src/common/configs/appconstant.config';

@Global()
@Module({
  imports: [
    ClientsModule.register([
      {
        name: 'AUTHENTICATION_PACKAGE',
        transport: Transport.GRPC,
        options: {
          package: 'authentication',
          protoPath: join(
            __dirname,
            '..',
            '..',
            'proto/identity/authentication.proto',
          ),
          url: appConstantConfig.getGrpcIdentityServiceUri(),
          loader: {
            keepCase: true,
            longs: String,
            enums: String,
            defaults: true,
            oneofs: true,
          },
          channelOptions: {
            'grpc.keepalive_time_ms': 30000,
            'grpc.keepalive_timeout_ms': 5000,
            'grpc.keepalive_permit_without_calls': 1,
          },
        },
      },
    ]),
  ],
  exports: [ClientsModule],
})
export class AuthModule {}
