import { GrpcError } from '../filters/grpc-error.interface';

export function isGrpcException(
  err: GrpcError,
): err is { code: number; details: string } {
  return err && typeof err.code === 'number';
}

export function grpcToHttp(code: number): number {
  switch (code) {
    case 16:
      return 401; // UNAUTHENTICATED
    case 7:
      return 403; // PERMISSION_DENIED
    case 5:
      return 404; // NOT_FOUND
    case 3:
      return 400; // INVALID_ARGUMENT
    default:
      return 500;
  }
}
