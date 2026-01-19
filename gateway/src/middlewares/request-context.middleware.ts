import { Injectable, Logger, NestMiddleware } from '@nestjs/common';
import { requestContext } from 'src/common/contexts/request-context';

@Injectable()
export class RequestContextMiddleware implements NestMiddleware {
  use(req: any, res: any, next: (error?: any) => void) {
    requestContext.run({}, () => {
      Logger.log('ALS context created');
      next();
    });
  }
}
