/**
 * Copyright 2018, leezhenghui@gmail.com.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package hello.spring.cloud.svc.ifw.runtime.interceptor;

import hello.spring.cloud.svc.ifw.util.ContextProvider;
import hello.spring.cloud.svc.ifw.annotation.QoS;
import hello.spring.cloud.svc.ifw.runtime.Interceptor;
import hello.spring.cloud.svc.ifw.runtime.RuntimeContext;
import hello.spring.cloud.svc.ifw.runtime.ServiceRuntimeException;
import org.springframework.kafka.core.KafkaOperations;
import org.springframework.kafka.core.KafkaTemplate;

import java.io.Serializable;
import java.util.Date;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class CounterInterceptor extends Interceptor{

    public static class CounterEvent implements Serializable {

        private static final long serialVersionUID = 1L;

        public String getUuid() {
            return uuid;
        }

        private String uuid;

        public CounterEvent() {
            this.uuid = UUID.randomUUID().toString();
        }

        private Date timestamp;

        public Date getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(Date timestamp) {
            this.timestamp = timestamp;
        }

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }

        private int count;

        public String toString() {

            StringBuffer sb = new StringBuffer();
            sb.append("UUID:").append(this.uuid).append("; count:").append(this.count).append("; timestamp:").append(this.timestamp.toString());

            return sb.toString();
        }

    }

    public static interface TxAwaredAction {
        public void run() throws ServiceRuntimeException;
    }

    private static String TOPIC_PROPERTY_NAME = "topic";
    private static String KAFKA_TEMPLATE_BEAN_NAME = "kafkaTemplateBeanName";
    private static String SEND_TIMEOUT = "send_timeout";

    private KafkaTemplate kt;
    private String topic;
    private int timeout = 10;

    public CounterInterceptor(int weight, Properties conf) {
        super(weight, conf);
        this.kt = (KafkaTemplate) ContextProvider.getBean(conf.getProperty(KAFKA_TEMPLATE_BEAN_NAME));
        this.topic = conf.getProperty(TOPIC_PROPERTY_NAME);

        if (conf.getProperty(SEND_TIMEOUT) != null && (! "".equals(conf.getProperty(SEND_TIMEOUT).trim()))) {
            this.timeout = Integer.parseInt(conf.getProperty(SEND_TIMEOUT));
        }
    }

    @Override
    public void processRequest(RuntimeContext ctx) {

        CounterEvent ce = new CounterEvent();
        ce.setCount(1);
        ce.setTimestamp(new Date());

        try {
            this.kt.send(this.topic, ce).get(this.timeout, TimeUnit.SECONDS);
        } catch (ExecutionException err) {
            throw new ServiceRuntimeException(this.QName(), "ExecutionException", err);
        } catch (TimeoutException | InterruptedException err) {
            throw new ServiceRuntimeException(this.QName(), "TimeoutException", err);
        }
    }

    @Override
    public void invoke(RuntimeContext ctx) {
//        TxAwaredAction txAwaredAction = new TxAwaredAction() {
//            @Transactional(transactionManager = "chainedTxMgrt")
//            public void run() throws ServiceRuntimeException{
//                CounterInterceptor.super.invoke(ctx);
//                if (ctx.getFault() != null) {
//                    // rollback
//                    throw ctx.getFault();
//                }
//            }
//        };
//
//        try {
//            txAwaredAction.run();
//        } catch (ServiceRuntimeException ser) {
//            // do nothing
//        }

        this.kt.executeInTransaction(new KafkaOperations.OperationsCallback() {
            @Override
            public Object doInOperations(KafkaOperations kafkaOperations) {
                CounterInterceptor.super.invoke(ctx);
                if (ctx.getFault() != null) {
                    throw ctx.getFault();
                }
                return true;
            }
        });
    }

    @Override
    public void processResponse(RuntimeContext ctx) {
        // do nothing
    }

    @Override
    public void processFault(RuntimeContext ctx) {
        // do nothing
    }

    @Override
    public String QName() {
        return "Invocation.Counter";
    }

    @Override
    public boolean accept(QoS[] annotation) {
        return true;
    }
}
