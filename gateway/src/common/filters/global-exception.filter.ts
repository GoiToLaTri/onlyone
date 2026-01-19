import {
  ArgumentsHost,
  Catch,
  ExceptionFilter,
  HttpException,
} from '@nestjs/common';
import { Request, Response } from 'express';
import { grpcToHttp, isGrpcException } from '../grpc';
import { GrpcError } from './grpc-error.interface';

@Catch()
export class GlobalExceptionFilter implements ExceptionFilter {
  catch(exception: unknown, host: ArgumentsHost) {
    const ctx = host.switchToHttp();
    const response = ctx.getResponse<Response>();
    const request = ctx.getRequest<Request>();
    console.error(exception);

    if (exception instanceof HttpException) {
      const status = exception.getStatus();
      const body = exception.getResponse();

      response.status(status).json({
        statusCode: status,
        message:
          typeof body === 'string'
            ? body
            : (body as { message: string }).message,
        path: request.url,
        timestamp: new Date().toISOString(),
      });
      return;
    }

    if (isGrpcException(exception as GrpcError)) {
      const ex: GrpcError = exception as GrpcError;
      const httpStatus = grpcToHttp(ex.code);

      response.status(httpStatus).json({
        statusCode: httpStatus,
        message: ex.details,
        path: request.url,
        timestamp: new Date().toISOString(),
      });
      return;
    }

    response.status(500).json({
      statusCode: 500,
      message: 'Internal server error',
      path: request.url,
      timestamp: new Date().toISOString(),
    });
  }
}
