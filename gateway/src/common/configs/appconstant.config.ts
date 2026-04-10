class AppConstantConfig {
  private port: number = Number(process.env.PORT || 8000);
  private grpcIdentityServiceUri: string =
    process.env.GRPC_IDENTITY_SERVICE_URI || '';
  private grpcUserServiceUri: string = process.env.GRPC_USER_SERVICE_URI || '';

  public getPort(): number {
    return this.port;
  }

  public getGrpcIdentityServiceUri(): string {
    return this.grpcIdentityServiceUri;
  }

  public getGrpcUserServiceUri(): string {
    return this.grpcUserServiceUri;
  }
}

export const appConstantConfig: AppConstantConfig = new AppConstantConfig();
