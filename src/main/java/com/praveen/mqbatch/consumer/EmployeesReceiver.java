package com.praveen.mqbatch.consumer;

import com.praveen.mqbatch.entity.Employee;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class EmployeesReceiver {

  @RabbitListener(queues = {"employees_queue"})
  public void receiveMessages(Employee employee) {
    log.info("Received from Queue " + employee);
  }
}
