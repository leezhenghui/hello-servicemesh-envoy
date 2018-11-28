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
package hello.spring.cloud.svc.ifw.runtime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

enum InteractionPhase {
    REQUEST,
    RESPONSE,
    FAULT
}

public abstract class Interceptor implements Invoker, Comparable {

    private static Logger logger = LoggerFactory.getLogger(Interceptor.class);

    private Invoker nexter;
    private int weight;

    public abstract void processRequest(RuntimeContext ctx);
    public abstract void processResponse(RuntimeContext ctx);
    public abstract void processFault(RuntimeContext ctx);

    public Interceptor(int weight, Properties conf) {
        this.weight = weight;
    }

    public void setNext(Invoker nexter) {
        this.nexter = nexter;
    }

    public Invoker getNext() {
        return this.nexter;
    }

    @Override
    public void invoke(RuntimeContext ctx) {
        InteractionPhase phase = InteractionPhase.REQUEST;
        try {
            logger.debug(this.QName() + ".processRequest() [Enter]");
            processRequest(ctx);
            logger.debug(this.QName() + ".processRequest() [Exit]");
            getNext().invoke(ctx);
            if (ctx.getFault() == null) {
                phase = InteractionPhase.RESPONSE;
                logger.debug(this.QName() + ".processResponse() [Enter]");
                processResponse(ctx);
                logger.debug(this.QName() + ".processResponse() [Exit]");
            } else {
                phase = InteractionPhase.FAULT;
                logger.debug(this.QName() + ".processFault() [Enter]");
                processFault(ctx);
                logger.debug(this.QName() + ".processFault() [Exit]");
            }
        } catch(ServiceRuntimeException sre) {
            logger.error(sre.getMessage(), sre.getDetails());
            if (phase != InteractionPhase.FAULT) {
                ctx.setFault(sre);
                logger.debug(this.QName() + ".processFault() [Enter]");
                processFault(ctx);
                logger.debug(this.QName() + ".processFault() [Exit]");
            }
        } catch(Throwable re) {
            logger.error(re.getMessage(), re);
            if (phase != InteractionPhase.FAULT) {
                ServiceRuntimeException sre = new ServiceRuntimeException(this.QName(), re.getMessage(), re);
                ctx.setFault(sre);
                logger.debug(this.QName() + ".processFault() [Enter]");
                processFault(ctx);
                logger.debug(this.QName() + ".processFault() [Exit]");
            }
        }
    }

    @Override
    public int compareTo(Object o) {
        if (! (o instanceof Interceptor)) {
            return -1;
        }

        Interceptor l = (Interceptor) o;

        if (this.weight == l.weight) {
            return 0;
        }

        if (this.weight > l.weight) {
            return -1;
        }

        return 1;
    }
}
