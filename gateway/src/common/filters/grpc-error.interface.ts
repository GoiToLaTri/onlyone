export interface GrpcError {
  code: number; // gRPC status code (3,5,7,16...)
  details: string; // description từ server
  metadata?: unknown; // Metadata (ít khi dùng)
}
