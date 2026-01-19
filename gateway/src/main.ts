import { NestFactory } from '@nestjs/core';
import { AppModule } from './app.module';
import { appConstantConfig } from './common/configs/appconstant.config';
import { Logger } from '@nestjs/common';
import { MicroserviceOptions, Transport } from '@nestjs/microservices';
import { join } from 'path';
import { GlobalExceptionFilter } from './common/filters/global-exception.filter';

async function bootstrap() {
  const app = await NestFactory.create(AppModule);

  app.connectMicroservice<MicroserviceOptions>({
    transport: Transport.GRPC,
    options: {
      package: 'user',
      protoPath: join(__dirname, './proto/user/user-service.proto'),
    },
  });

  app.useGlobalFilters(new GlobalExceptionFilter());

  await app.listen(appConstantConfig.getPort(), () => {
    Logger.log(
      `Gateway listening on port: ${appConstantConfig.getPort()}`,
      'NestApplication',
    );
  });
}

void bootstrap();
