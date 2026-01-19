class AppConstantConfig {
  private port: number = Number(process.env.PORT || 8000);

  public getPort(): number {
    return this.port;
  }
}

export const appConstantConfig: AppConstantConfig = new AppConstantConfig();
