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

public class ServiceRuntimeException extends RuntimeException {

    private Throwable details;
    private String causedBy;

    public Throwable getDetails() {
        return details;
    }

    public String getCausedBy() {
        return causedBy;
    }

    public String getReason() {
        return reason;
    }

    private String reason;
    public ServiceRuntimeException(String causedBy, String reason, Throwable details) {
        super(reason, details);
        this.causedBy = causedBy;
        this.reason = reason;
        this.details = details;
    }
}
