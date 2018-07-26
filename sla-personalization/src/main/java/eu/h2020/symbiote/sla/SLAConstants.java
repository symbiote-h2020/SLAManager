/**
 * Copyright 2017 Atos
 * Contact: Atos <jose.sanchezm@atos.net>
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package eu.h2020.symbiote.sla;

public interface SLAConstants {
  String EXCHANGE_NAME_SLAM = "symbIoTe.slam";
  String VIOLATION_KEY = "federation.sla.violation";

  String SLA_REGISTRATION_QUEUE_NAME = "symbIoTe.fm.sla.create";
  String SLA_UPDATE_QUEUE_NAME = "symbIoTe.fm.sla.update";
  String SLA_UNREGISTRATION_QUEUE_NAME = "symbIoTe.fm.sla.unregistration";
}
