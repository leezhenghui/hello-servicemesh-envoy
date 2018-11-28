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

import hello.spring.cloud.svc.ifw.annotation.QoS;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.AopProxyUtils;

import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;

public class RuntimeInvoker {

    private static Logger logger = LoggerFactory.getLogger(RuntimeInvoker.class);

    private ConcurrentHashMap<String, Invocable> cache = new ConcurrentHashMap<String, Invocable>();
    public static RuntimeInvoker INSTANCE = new RuntimeInvoker();

    private RuntimeInvoker() {
    }

    public Object invoke(ProceedingJoinPoint pjp) {
        Object reval = null;

        try {
            //Not support method override so far
            String key = pjp.getSignature().getDeclaringTypeName() + "." + pjp.getSignature().getName();
            Invocable invocable = cache.get(key);
            if (invocable == null) {
                invocable = wrap(pjp);
                logger.debug("Create invocation wrapper for \"" + key + "\"");
                cache.put(key, invocable);
            }

            reval = invocable.invoke(pjp.getTarget(), pjp.getArgs());
        } catch (ServiceRuntimeException sre) {
            throw sre;
        } catch(Throwable err) {
            logger.error(err.getMessage(), err);
            ServiceRuntimeException sre = new ServiceRuntimeException("sys.ifw", err.getMessage(), err);
            throw sre;
        }

        return reval;
    }

    private Invocable wrap(ProceedingJoinPoint pjp) {

        Invocable invocable = null;
        try {
            Object target = pjp.getTarget();
            Class targetClass = AopProxyUtils.ultimateTargetClass(target);
            MethodSignature methodSig = (MethodSignature) pjp.getSignature();
            Method targetMethod = targetClass.getDeclaredMethod(methodSig.getName(), methodSig.getParameterTypes());
            QoS[] annotations = targetMethod.getAnnotationsByType(QoS.class);

            invocable = new Invocable(targetMethod, annotations[0].value());
            return invocable;
        } catch (NoSuchMethodException e) {
            String reason = "Can not find method \"" + pjp.getSignature().getDeclaringTypeName() + "." + pjp.getSignature().getName() + "\"";
            logger.error(reason, e);
            throw new ServiceRuntimeException("ifw", reason, e);
        }
    }
}
