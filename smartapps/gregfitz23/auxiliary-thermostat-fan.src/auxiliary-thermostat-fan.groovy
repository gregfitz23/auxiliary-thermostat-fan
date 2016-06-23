/**
 *  Auxilliary Thermostat Fan
 *
 *  Copyright 2016 Greg Fitzgerald
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the License at:
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 *  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 *  for the specific language governing permissions and limitations under the License.
 *
 */
definition(
    name: "Auxiliary Thermostat Fan",
    namespace: "gregfitz23",
    author: "Greg Fitzgerald",
    description: "Toggle an auxiliary fan to blow cool or hot air when the thermostat is cooling or heating",
    category: "Green Living",
    iconUrl: "http://cdn.device-icons.smartthings.com/Appliances/appliances11-icn@2x.png",
    iconX2Url: "http://cdn.device-icons.smartthings.com/Appliances/appliances11-icn@2x.png"
)


preferences {
    section("Auxilliary Fan") {
        input "fans", "capability.switch", title: "Auxilliary Fan", multiple: true
    }
    
    section("Thermostat") {
    	input "thermostat", "capability.thermostat", title: "Thermostat"
    }
    
    section("Conditions") {
    	input(name: "mode", type: "enum", title: "Mode", options: ["cool", "heat", "both"])
    }
}

def installed() {
	log.debug "Installed with settings: ${settings}"

	initialize()
}

def updated() {
	log.debug "Updated with settings: ${settings}"

	unsubscribe()
	initialize()
}

def initialize() {
	state.fanRunning = false;
    
    subscribe(thermostat, "thermostatMode", "checkThings");
    subscribe(thermostat, "thermostatFanMode", "checkThings");
}

def checkThings(evt) {
    def thermostatMode = settings.thermostat.currentValue('thermostatMode')
    def thermostatFanMode = settings.thermostat.currentValue('thermostatFanMode')
    def selectedMode = settings.mode;
    
    log.debug "Thermostat: $thermostatMode, ThermostatFanMode: $thermostatFanMode, Mode: $selectedMode, Fan Running: ${state.fanRunning}"
    
    def shouldRun = thermostatFanMode == "on" && (selectedMode == "both" || selectedMode == thermostatMode);
    
    if(shouldRun && !state.fanRunning) {
    	fans.on();
        state.fanRunning = true;
    } else if(!shouldRun && state.fanRunning) {
    	fans.off();
        state.fanRunning = false;
    }
}
