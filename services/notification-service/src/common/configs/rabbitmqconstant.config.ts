class RabbitMQConstantConfig {
  // ---------- Queue constant ----------
  private notificationServiceQueue: string = 'notification-service-queue';

  // ---------- Exchange constant ----------
  private forceLoggedOutExchange: string = 'force-logged-out';

  // ---------- Routing key constant ----------
  private forceLoggedOutRoutingKey: string = 'force-logged-out';

  public getNotificationServiceQueue(): string {
    return this.notificationServiceQueue;
  }

  public getForceLoggedOutExchange(): string {
    return this.forceLoggedOutExchange;
  }

  public getForceLoggedOutRoutingKey(): string {
    return this.forceLoggedOutRoutingKey;
  }
}

export const rabbitMQConstantConfig: RabbitMQConstantConfig =
  new RabbitMQConstantConfig();
