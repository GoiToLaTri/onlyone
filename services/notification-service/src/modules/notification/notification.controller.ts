import { Controller } from '@nestjs/common';
import { Ctx, EventPattern, Payload, RmqContext } from '@nestjs/microservices';
import { Channel, ConsumeMessage } from 'amqplib';
import { rabbitMQConstantConfig } from 'src/common/configs/rabbitmqconstant.config';

@Controller()
export class NotificationController {
  @EventPattern(rabbitMQConstantConfig.getForceLoggedOutRoutingKey())
  loggedMessage(@Payload() data: any, @Ctx() context: RmqContext) {
    const originalMsg: ConsumeMessage = context.getMessage() as ConsumeMessage;
    console.log(':::: Data: ', data);
    console.log(':::: Routing Key:', originalMsg.fields.routingKey);
    console.log(':::: Buffer:', originalMsg.content.toString());

    const channel: Channel = context.getChannelRef() as Channel;
    channel.ack(originalMsg);
  }
}
