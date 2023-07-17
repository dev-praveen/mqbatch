package com.praveen.mqbatch.config;

import com.praveen.mqbatch.entity.Employee;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.amqp.AmqpItemWriter;
import org.springframework.batch.item.amqp.builder.AmqpItemWriterBuilder;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;
import javax.sql.DataSource;

@Slf4j
@Configuration
public class MqBatchConfiguration {

  @Autowired private DataSource dataSource;
  @Autowired private RabbitTemplate rabbitTemplate;

  @Bean
  public JdbcCursorItemReader<Employee> itemReader() {
    return new JdbcCursorItemReaderBuilder<Employee>()
        .dataSource(this.dataSource)
        .name("employeesReader")
        .sql("select emp_no, birth_date, first_name, last_name, gender, hire_date from employees")
        .rowMapper(
            (resultSet, rowNum) ->
                Employee.builder()
                    .empNo(resultSet.getInt("emp_no"))
                    .birthDate(resultSet.getString("birth_date"))
                    .firstName(resultSet.getString("first_name"))
                    .lastName(resultSet.getString("last_name"))
                    .gender(resultSet.getString("gender"))
                    .hireDate(resultSet.getString("hire_date"))
                    .build())
        .verifyCursorPosition(false)
        .build();
  }

  @Bean
  public AmqpItemWriter<Employee> itemWriter() {
    return new AmqpItemWriterBuilder<Employee>().amqpTemplate(rabbitTemplate).build();
  }

  @Bean
  public Job job(
      JobRepository jobRepository, JobCompletionNotificationListener listener, Step step) {
    return new JobBuilder("job", jobRepository)
        .incrementer(new RunIdIncrementer())
        .listener(listener)
        .flow(step)
        .end()
        .build();
  }

  @Bean
  public TaskExecutor taskExecutor() {
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setThreadNamePrefix("Custom-Thread_");
    executor.setCorePoolSize(5);
    executor.setMaxPoolSize(10);
    executor.setQueueCapacity(30);
    return executor;
  }

  @Bean
  public Step step(
      JobRepository jobRepository,
      PlatformTransactionManager transactionManager,
      AmqpItemWriter<Employee> itemWriter) {
    return new StepBuilder("step", jobRepository)
        .<Employee, Employee>chunk(100, transactionManager)
        .reader(itemReader())
        .writer(itemWriter)
        .taskExecutor(taskExecutor())
        .build();
  }
}
