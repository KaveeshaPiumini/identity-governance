/*
 * Copyright (c) 2025, WSO2 LLC. (http://www.wso2.com).
 *
 * WSO2 LLC. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.carbon.identity.captcha.listener;

import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.wso2.carbon.identity.captcha.exception.CaptchaException;
import org.wso2.carbon.identity.captcha.util.CaptchaUtil;
import org.wso2.carbon.identity.flow.execution.engine.exception.FlowEngineClientException;
import org.wso2.carbon.identity.flow.execution.engine.exception.FlowEngineException;
import org.wso2.carbon.identity.flow.execution.engine.exception.FlowEngineServerException;
import org.wso2.carbon.identity.flow.execution.engine.model.FlowExecutionContext;
import org.wso2.carbon.identity.flow.execution.engine.model.FlowExecutionStep;
import org.wso2.carbon.identity.flow.mgt.Constants;
import org.wso2.carbon.identity.flow.mgt.model.ComponentDTO;
import org.wso2.carbon.identity.flow.mgt.model.DataDTO;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;

import static org.mockito.ArgumentMatchers.anyString;
import static org.wso2.carbon.identity.captcha.listener.CaptchaFlowExecutionListener.CAPTCHA_ENABLED;
import static org.wso2.carbon.identity.captcha.listener.CaptchaFlowExecutionListener.CAPTCHA_KEY;
import static org.wso2.carbon.identity.captcha.listener.CaptchaFlowExecutionListener.CAPTCHA_RESPONSE;

/**
 * Unit test for {@link CaptchaFlowExecutionListener}.
 */
public class CaptchaFlowExecutionListenerTest {

    private static CaptchaFlowExecutionListener captchaFlowExecutionListener;

    @BeforeMethod
    public void setUp() {

        captchaFlowExecutionListener = new CaptchaFlowExecutionListener();
    }

    @AfterMethod
    public void tearDown() {

    }

    @Test
    public void testDoPostExecute() throws FlowEngineException {

        FlowExecutionStep registrationStep = getRegistrationStep();
        FlowExecutionContext registrationContext = getRegistrationContext();
        try (MockedStatic<CaptchaUtil> captchaUtilMockedStatic = Mockito.mockStatic(CaptchaUtil.class)) {
            captchaUtilMockedStatic.when(CaptchaUtil::reCaptchaSiteKey).thenReturn("captchaKeyValue");
            captchaUtilMockedStatic.when(() -> CaptchaUtil.isReCaptchaEnabledForFlow(anyString(), anyString()))
                    .thenReturn(true);
            captchaFlowExecutionListener.doPostExecute(registrationStep, registrationContext);
            Assert.assertTrue(registrationStep.getData().getComponents().get(0).getConfigs().containsKey(CAPTCHA_KEY));
            Assert.assertTrue(registrationContext.getCurrentStepInputs().get("action1").contains(CAPTCHA_RESPONSE));
            Assert.assertTrue(registrationContext.getCurrentRequiredInputs().get("action1").contains(CAPTCHA_RESPONSE));
            Assert.assertTrue((boolean) registrationContext.getProperty(CAPTCHA_ENABLED));
        }
    }

    @Test(expectedExceptions = FlowEngineClientException.class)
    public void testDoPreExecuteMissingCaptchaResponse() throws FlowEngineException {

        FlowExecutionContext registrationContext = getRegistrationContext();
        registrationContext.setProperty(CAPTCHA_ENABLED, true);
        registrationContext.getUserInputData().put(CAPTCHA_RESPONSE, "");
        try (MockedStatic<CaptchaUtil> captchaUtilMockedStatic = Mockito.mockStatic(CaptchaUtil.class)) {
            captchaUtilMockedStatic.when(() -> CaptchaUtil.isReCaptchaEnabledForFlow(anyString(), anyString()))
                    .thenReturn(true);
            captchaFlowExecutionListener.doPreExecute(registrationContext);
        }
    }

    @Test(expectedExceptions = FlowEngineClientException.class)
    public void testDoPreExecuteInvalidCaptcha() throws FlowEngineException {

        FlowExecutionContext registrationContext = getRegistrationContext();
        registrationContext.setProperty(CAPTCHA_ENABLED, true);
        registrationContext.getUserInputData().put(CAPTCHA_RESPONSE, "someResponse");
        try (MockedStatic<CaptchaUtil> captchaUtilMockedStatic = Mockito.mockStatic(CaptchaUtil.class)) {
            captchaUtilMockedStatic.when(() -> CaptchaUtil.isValidCaptcha(anyString())).thenReturn(false);
            captchaUtilMockedStatic.when(() -> CaptchaUtil.isReCaptchaEnabledForFlow(anyString(), anyString()))
                    .thenReturn(true);
            captchaFlowExecutionListener.doPreExecute(registrationContext);
        }
    }

    @Test
    public void testDoPostExecuteCaptchaDisabled() throws FlowEngineException {

        FlowExecutionStep step = getRegistrationStep();
        FlowExecutionContext context = getRegistrationContext();

        try (MockedStatic<CaptchaUtil> captchaUtil = Mockito.mockStatic(CaptchaUtil.class)) {
            captchaUtil.when(() -> CaptchaUtil.isReCaptchaEnabledForFlow(anyString(), anyString())).thenReturn(false);
            captchaFlowExecutionListener.doPostExecute(step, context);
            Assert.assertFalse(step.getData().getComponents().get(0).getConfigs().containsKey(CAPTCHA_KEY));
            Assert.assertFalse(context.getCurrentStepInputs().get("action1").contains(CAPTCHA_RESPONSE));
            Assert.assertFalse(context.getCurrentRequiredInputs().get("action1").contains(CAPTCHA_RESPONSE));
            Assert.assertNull(context.getProperty(CAPTCHA_ENABLED));
        }
    }

    @Test
    public void testDoPostExecuteNullComponents() throws FlowEngineException {

        FlowExecutionStep step = new FlowExecutionStep.Builder()
                .stepType(Constants.StepTypes.VIEW)
                .flowId("flowId")
                .data(new DataDTO.Builder().components(null).build())
                .build();
        FlowExecutionContext context = getRegistrationContext();

        try (MockedStatic<CaptchaUtil> captchaUtil = Mockito.mockStatic(CaptchaUtil.class)) {

            captchaUtil.when(() -> CaptchaUtil.isReCaptchaEnabledForFlow(anyString(), anyString())).thenReturn(true);
            captchaFlowExecutionListener.doPostExecute(step, context);
            Assert.assertNull(context.getProperty(CAPTCHA_ENABLED));
            Assert.assertFalse(context.getCurrentStepInputs().get("action1").contains(CAPTCHA_RESPONSE));
            Assert.assertFalse(context.getCurrentRequiredInputs().get("action1").contains(CAPTCHA_RESPONSE));
        }
    }

    @Test
    public void testDoPostExecuteNonCaptchaComponent() throws FlowEngineException {

        ComponentDTO nonCaptchaComponent = new ComponentDTO.Builder()
                .id("input_1")
                .type("TEXT_INPUT")
                .variant("GENERIC")
                .build();
        FlowExecutionStep step = new FlowExecutionStep.Builder()
                .stepType(Constants.StepTypes.VIEW)
                .flowId("flowId")
                .data(new DataDTO.Builder().components(Collections.singletonList(nonCaptchaComponent)).build())
                .build();
        FlowExecutionContext context = getRegistrationContext();

        try (MockedStatic<CaptchaUtil> captchaUtil = Mockito.mockStatic(CaptchaUtil.class)) {

            captchaUtil.when(() -> CaptchaUtil.isReCaptchaEnabledForFlow(anyString(), anyString())).thenReturn(true);
            captchaFlowExecutionListener.doPostExecute(step, context);
            Assert.assertFalse(step.getData().getComponents().get(0).getConfigs().containsKey(CAPTCHA_KEY));
            Assert.assertFalse(context.getCurrentStepInputs().get("action1").contains(CAPTCHA_RESPONSE));
            Assert.assertFalse(context.getCurrentRequiredInputs().get("action1").contains(CAPTCHA_RESPONSE));
            Assert.assertNull(context.getProperty(CAPTCHA_ENABLED));
        }
    }

    @Test
    public void testDoPreExecuteCaptchaDisabled() throws FlowEngineException {

        FlowExecutionContext context = getRegistrationContext();
        try (MockedStatic<CaptchaUtil> captchaUtil = Mockito.mockStatic(CaptchaUtil.class)) {
            captchaUtil.when(() -> CaptchaUtil.isReCaptchaEnabledForFlow(anyString(), anyString()))
                    .thenReturn(false);
            context.setProperty(CAPTCHA_ENABLED, false);
            captchaFlowExecutionListener.doPreExecute(context);
            // No exception means success; make sure captcha remains disabled.
            Assert.assertEquals(context.getProperty(CAPTCHA_ENABLED), false);
        }
    }

    @Test
    public void testDoPreExecuteSkipsCaptchaValidationWhenCaptchaPropertyNotSet() throws FlowEngineException {

        FlowExecutionContext context = getRegistrationContext();
        try (MockedStatic<CaptchaUtil> captchaUtil = Mockito.mockStatic(CaptchaUtil.class)) {
            captchaUtil.when(() -> CaptchaUtil.isReCaptchaEnabledForFlow(anyString(), anyString()))
                    .thenReturn(true);
            Assert.assertTrue(captchaFlowExecutionListener.doPreExecute(context));
        }
    }

    @Test(expectedExceptions = FlowEngineServerException.class)
    public void testDoPreExecuteCaptchaThrowsServerException() throws FlowEngineException {

        FlowExecutionContext context = getRegistrationContext();
        context.setProperty(CAPTCHA_ENABLED, true);
        context.getUserInputData().put(CAPTCHA_RESPONSE, "someResponse");

        try (MockedStatic<CaptchaUtil> captchaUtil = Mockito.mockStatic(CaptchaUtil.class)) {
            captchaUtil.when(() -> CaptchaUtil.isValidCaptcha(anyString()))
                    .thenThrow(new CaptchaException("Internal error"));
            captchaUtil.when(() -> CaptchaUtil.isReCaptchaEnabledForFlow(anyString(), anyString())).thenReturn(true);
            captchaFlowExecutionListener.doPreExecute(context);
        }
    }

    private FlowExecutionContext getRegistrationContext() {

        FlowExecutionContext registrationContext = new FlowExecutionContext();
        registrationContext.setTenantDomain("test.com");
        registrationContext.setContextIdentifier("contextId");
        registrationContext.getCurrentRequiredInputs().put("action1", new HashSet<>());
        registrationContext.getCurrentStepInputs().put("action1", new HashSet<>());
        return registrationContext;
    }

    private FlowExecutionStep getRegistrationStep() {

        ComponentDTO componentDTO = new ComponentDTO.Builder()
                .id("captcha_f12v")
                .type("CAPTCHA")
                .variant("RECAPTCHA_V2")
                .build();
        return new FlowExecutionStep.Builder()
                .stepType(Constants.StepTypes.VIEW)
                .flowId("flowId")
                .data(new DataDTO.Builder()
                        .components(Collections.singletonList(componentDTO))
                        .additionalData(new HashMap<>())
                        .build()).build();
    }
}
