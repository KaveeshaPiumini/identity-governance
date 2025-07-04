/*
 * Copyright (c) 2017-2025, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.carbon.identity.recovery.connector;

import org.apache.commons.lang.StringUtils;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import org.wso2.carbon.identity.application.common.model.Property;
import org.wso2.carbon.identity.core.util.IdentityUtil;
import org.wso2.carbon.identity.governance.IdentityGovernanceException;
import org.wso2.carbon.identity.governance.IdentityMgtConstants;
import org.wso2.carbon.identity.recovery.IdentityRecoveryConstants;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;
import static org.wso2.carbon.identity.governance.IdentityGovernanceUtil.getPropertyObject;

/**
 * This class does unit test coverage for SelfRegistrationConfigImpl class.
 */
public class SelfRegistrationConfigImplTest {

    private MockedStatic<IdentityUtil> mockedIdentityUtil;
    private SelfRegistrationConfigImpl selfRegistrationConfigImpl;
    private static final String CONNECTOR_NAME = "self-sign-up";
    private static final String CATEGORY = "User Onboarding";
    private static final String FRIENDLY_NAME = "Self Registration";
    private static final String LIST_PURPOSE_PROPERTY_KEY = "_url_listPurposeSelfSignUp";
    private static final String SYSTEM_PURPOSE_GROUP = "SELF-SIGNUP";
    private static final String SIGNUP_PURPOSE_GROUP_TYPE = "SYSTEM";
    private static final String CALLBACK_URL = "/carbon/idpmgt/idp-mgt-edit-local.jsp?category=" + CATEGORY +
            "&subCategory=" + FRIENDLY_NAME;
    private static final String CONSENT_LIST_URL = "/carbon/consent/list-purposes.jsp?purposeGroup=" +
            SYSTEM_PURPOSE_GROUP + "&purposeGroupType=" + SIGNUP_PURPOSE_GROUP_TYPE;

    @BeforeMethod
    public void setUp() {

        mockedIdentityUtil = Mockito.mockStatic(IdentityUtil.class);
    }

    @BeforeTest
    public void Init() {

        selfRegistrationConfigImpl = new SelfRegistrationConfigImpl();
    }

    @AfterMethod
    public void tearDown() {

        mockedIdentityUtil.close();
    }

    @Test
    public void testGetName() {

        assertEquals(selfRegistrationConfigImpl.getName(), CONNECTOR_NAME);
    }

    @Test
    public void testGetFriendlyName() {

        assertEquals(selfRegistrationConfigImpl.getFriendlyName(), FRIENDLY_NAME);
    }

    @Test
    public void testGetCategory() {

        assertEquals(selfRegistrationConfigImpl.getCategory(), CATEGORY);
    }

    @Test
    public void testGetSubCategory() {

        assertEquals(selfRegistrationConfigImpl.getSubCategory(), "DEFAULT");
    }

    @Test
    public void testGetOrder() {

        assertEquals(selfRegistrationConfigImpl.getOrder(), 0);
    }

    @Test
    public void testGetPropertyNameMapping() {

        Map<String, String> nameMappingExpected = new HashMap<String, String>();
        nameMappingExpected.put(IdentityRecoveryConstants.ConnectorConfig.ENABLE_SELF_SIGNUP, "User self registration");
        nameMappingExpected.put(IdentityRecoveryConstants.ConnectorConfig.ACCOUNT_LOCK_ON_CREATION,
                "Lock user account on creation");
        nameMappingExpected.put(IdentityRecoveryConstants.ConnectorConfig.SELF_REGISTRATION_SEND_OTP_IN_EMAIL,
                "Send OTP in e-mail");
        nameMappingExpected.put(IdentityRecoveryConstants.ConnectorConfig.SELF_REGISTRATION_EMAIL_OTP_ENABLE,
                "Enable email OTP");
        nameMappingExpected.put(
                IdentityRecoveryConstants.ConnectorConfig.SELF_REGISTRATION_USE_UPPERCASE_CHARACTERS_IN_OTP,
                "Include uppercase characters in OTP");
        nameMappingExpected.put(
                IdentityRecoveryConstants.ConnectorConfig.SELF_REGISTRATION_USE_LOWERCASE_CHARACTERS_IN_OTP,
                "Include lowercase characters in OTP");
        nameMappingExpected.put(IdentityRecoveryConstants.ConnectorConfig.SELF_REGISTRATION_USE_NUMBERS_IN_OTP,
                "Include numbers in OTP");
        nameMappingExpected.put(IdentityRecoveryConstants.ConnectorConfig.SELF_REGISTRATION_OTP_LENGTH,
                "OTP length");
        nameMappingExpected.put(IdentityRecoveryConstants.ConnectorConfig.SEND_CONFIRMATION_NOTIFICATION,
                "Enable Account Confirmation On Creation");
        nameMappingExpected.put(IdentityRecoveryConstants.ConnectorConfig.SHOW_USERNAME_UNAVAILABILITY,
                "Show username unavailability");
        nameMappingExpected.put(IdentityRecoveryConstants.ConnectorConfig.SIGN_UP_NOTIFICATION_INTERNALLY_MANAGE,
                "Manage notifications sending internally");
        nameMappingExpected.put(IdentityRecoveryConstants.ConnectorConfig.SELF_REGISTRATION_RE_CAPTCHA,
                "Prompt reCaptcha");
        nameMappingExpected.put(
                IdentityRecoveryConstants.ConnectorConfig.SELF_REGISTRATION_VERIFICATION_CODE_EXPIRY_TIME,
                "User self registration verification link expiry time");
        nameMappingExpected.put(
                IdentityRecoveryConstants.ConnectorConfig.SELF_REGISTRATION_SMSOTP_VERIFICATION_CODE_EXPIRY_TIME,
                "User self registration SMS OTP expiry time");
        nameMappingExpected.put(IdentityRecoveryConstants.ConnectorConfig.SELF_REGISTRATION_SMS_OTP_REGEX,
                "User self registration SMS OTP regex");
        nameMappingExpected.put(IdentityRecoveryConstants.ConnectorConfig.SELF_REGISTRATION_CALLBACK_REGEX,
                "User self registration callback URL regex");
        nameMappingExpected.put(LIST_PURPOSE_PROPERTY_KEY, "Manage Self-Sign-Up purposes");
        nameMappingExpected.put(IdentityRecoveryConstants.ConnectorConfig.SELF_REGISTRATION_NOTIFY_ACCOUNT_CONFIRMATION,
                "Send sign up confirmation email");
        nameMappingExpected.put(IdentityRecoveryConstants.ConnectorConfig.RESEND_CONFIRMATION_RECAPTCHA_ENABLE,
                "Prompt reCaptcha on re-send confirmation");
        nameMappingExpected.put(IdentityRecoveryConstants.ConnectorConfig.SELF_REGISTRATION_AUTO_LOGIN,
                "Enable Auto Login After Account Confirmation");
        nameMappingExpected.put(IdentityRecoveryConstants.ConnectorConfig.SELF_REGISTRATION_AUTO_LOGIN_ALIAS_NAME,
                "Alias of the key used to sign to cookie");
        nameMappingExpected.put(IdentityRecoveryConstants.ConnectorConfig.ENABLE_DYNAMIC_REGISTRATION_PORTAL,
                "Enable dynamic registration portal");
        Map<String, String> nameMapping = selfRegistrationConfigImpl.getPropertyNameMapping();

        assertEquals(nameMapping, nameMappingExpected, "Maps are not equal");
    }

    @Test
    public void testGetPropertyDescriptionMapping() {

        Map<String, String> descriptionMappingExpected = new HashMap<>();
        descriptionMappingExpected.put(IdentityRecoveryConstants.ConnectorConfig.ENABLE_SELF_SIGNUP,
                "Allow user's to self register to the system.");
        descriptionMappingExpected.put(IdentityRecoveryConstants.ConnectorConfig.ACCOUNT_LOCK_ON_CREATION,
                "Lock self registered user account until e-mail verification.");
        descriptionMappingExpected.put(IdentityRecoveryConstants.ConnectorConfig.SELF_REGISTRATION_SEND_OTP_IN_EMAIL,
                "Enable to send OTP in verification e-mail instead of confirmation code.");
        descriptionMappingExpected.put(IdentityRecoveryConstants.ConnectorConfig.SELF_REGISTRATION_EMAIL_OTP_ENABLE,
                "Enable to send email OTP for self registration.");
        descriptionMappingExpected.put(
                IdentityRecoveryConstants.ConnectorConfig.SELF_REGISTRATION_USE_UPPERCASE_CHARACTERS_IN_OTP,
                "Enable to include uppercase characters in SMS and e-mail OTPs.");
        descriptionMappingExpected.put(
                IdentityRecoveryConstants.ConnectorConfig.SELF_REGISTRATION_USE_LOWERCASE_CHARACTERS_IN_OTP,
                "Enable to include lowercase characters in SMS and e-mail OTPs.");
        descriptionMappingExpected.put(IdentityRecoveryConstants.ConnectorConfig.SELF_REGISTRATION_USE_NUMBERS_IN_OTP,
                "Enable to include numbers in SMS and e-mail OTPs.");
        descriptionMappingExpected.put(IdentityRecoveryConstants.ConnectorConfig.SELF_REGISTRATION_OTP_LENGTH,
                "Length of the OTP for SMS and e-mail verifications. OTP length must be 4-10.");
        descriptionMappingExpected.put(IdentityRecoveryConstants.ConnectorConfig.SEND_CONFIRMATION_NOTIFICATION,
                "Enable user account confirmation when the user account is not locked on creation");
        descriptionMappingExpected.put(IdentityRecoveryConstants.ConnectorConfig.SHOW_USERNAME_UNAVAILABILITY,
                "Show a descriptive error message to the user if the username is already taken. However, this may lead to username enumeration");
        descriptionMappingExpected.put(IdentityRecoveryConstants.ConnectorConfig.SIGN_UP_NOTIFICATION_INTERNALLY_MANAGE,
                "Disable if the client application handles notification sending");
        descriptionMappingExpected.put(IdentityRecoveryConstants.ConnectorConfig.SELF_REGISTRATION_RE_CAPTCHA,
                "Enable reCaptcha verification during self registration.");
        descriptionMappingExpected.put(
                IdentityRecoveryConstants.ConnectorConfig.SELF_REGISTRATION_VERIFICATION_CODE_EXPIRY_TIME,
                "Specify the expiry time in minutes for the verification link.");
        descriptionMappingExpected.put(
                IdentityRecoveryConstants.ConnectorConfig.SELF_REGISTRATION_SMSOTP_VERIFICATION_CODE_EXPIRY_TIME,
                "Specify the expiry time in minutes for the SMS OTP.");
        descriptionMappingExpected.put(IdentityRecoveryConstants.ConnectorConfig.SELF_REGISTRATION_SMS_OTP_REGEX,
                "Regex for SMS OTP in format [allowed characters]{length}. Supported character " +
                        "ranges are a-z, A-Z, 0-9. Minimum OTP length is " +
                        IdentityMgtConstants.MINIMUM_SMS_OTP_LENGTH);
        descriptionMappingExpected.put(IdentityRecoveryConstants.ConnectorConfig.SELF_REGISTRATION_CALLBACK_REGEX,
                "This prefix will be used to validate the callback URL.");
        descriptionMappingExpected.put(LIST_PURPOSE_PROPERTY_KEY, "Click here to manage Self-Sign-Up purposes");
        descriptionMappingExpected
                .put(IdentityRecoveryConstants.ConnectorConfig.SELF_REGISTRATION_NOTIFY_ACCOUNT_CONFIRMATION,
                        "Enable sending notification for self sign up confirmation.");
        descriptionMappingExpected.put(IdentityRecoveryConstants.ConnectorConfig.RESEND_CONFIRMATION_RECAPTCHA_ENABLE,
                "Prompt reCaptcha verification for resend confirmation");
        descriptionMappingExpected.put(IdentityRecoveryConstants.ConnectorConfig.SELF_REGISTRATION_AUTO_LOGIN,
                "User will be logged in automatically after completing the Account Confirmation ");
        descriptionMappingExpected.put(
                IdentityRecoveryConstants.ConnectorConfig.SELF_REGISTRATION_AUTO_LOGIN_ALIAS_NAME,
                "Alias of the key used to sign to cookie. The public key has to be imported to the keystore. ");
        descriptionMappingExpected.put(IdentityRecoveryConstants.ConnectorConfig.ENABLE_DYNAMIC_REGISTRATION_PORTAL,
                "Enable and allow users to register to the system using the dynamic registration portal.");

        Map<String, String> descriptionMapping = selfRegistrationConfigImpl.getPropertyDescriptionMapping();

        assertEquals(descriptionMapping, descriptionMappingExpected, "Maps are not equal");
    }

    @Test
    public void testGetPropertyNames() {

        List<String> propertiesExpected = new ArrayList<>();
        propertiesExpected.add(IdentityRecoveryConstants.ConnectorConfig.ENABLE_SELF_SIGNUP);
        propertiesExpected.add(IdentityRecoveryConstants.ConnectorConfig.ACCOUNT_LOCK_ON_CREATION);
        propertiesExpected.add(IdentityRecoveryConstants.ConnectorConfig.SELF_REGISTRATION_SEND_OTP_IN_EMAIL);
        propertiesExpected.add(IdentityRecoveryConstants.ConnectorConfig.SELF_REGISTRATION_EMAIL_OTP_ENABLE);
        propertiesExpected.add(
                IdentityRecoveryConstants.ConnectorConfig.SELF_REGISTRATION_USE_UPPERCASE_CHARACTERS_IN_OTP);
        propertiesExpected.add(
                IdentityRecoveryConstants.ConnectorConfig.SELF_REGISTRATION_USE_LOWERCASE_CHARACTERS_IN_OTP);
        propertiesExpected.add(IdentityRecoveryConstants.ConnectorConfig.SELF_REGISTRATION_USE_NUMBERS_IN_OTP);
        propertiesExpected.add(IdentityRecoveryConstants.ConnectorConfig.SELF_REGISTRATION_OTP_LENGTH);
        propertiesExpected.add(IdentityRecoveryConstants.ConnectorConfig.SEND_CONFIRMATION_NOTIFICATION);
        propertiesExpected.add(IdentityRecoveryConstants.ConnectorConfig.SHOW_USERNAME_UNAVAILABILITY);
        propertiesExpected.add(IdentityRecoveryConstants.ConnectorConfig.SIGN_UP_NOTIFICATION_INTERNALLY_MANAGE);
        propertiesExpected.add(IdentityRecoveryConstants.ConnectorConfig.SELF_REGISTRATION_RE_CAPTCHA);
        propertiesExpected
                .add(IdentityRecoveryConstants.ConnectorConfig.SELF_REGISTRATION_VERIFICATION_CODE_EXPIRY_TIME);
        propertiesExpected
                .add(IdentityRecoveryConstants.ConnectorConfig.SELF_REGISTRATION_SMSOTP_VERIFICATION_CODE_EXPIRY_TIME);
        propertiesExpected.add(IdentityRecoveryConstants.ConnectorConfig.SELF_REGISTRATION_SMS_OTP_REGEX);
        propertiesExpected.add(IdentityRecoveryConstants.ConnectorConfig.SELF_REGISTRATION_CALLBACK_REGEX);
        propertiesExpected.add(LIST_PURPOSE_PROPERTY_KEY);
        propertiesExpected.add(IdentityRecoveryConstants.ConnectorConfig.SELF_REGISTRATION_NOTIFY_ACCOUNT_CONFIRMATION);
        propertiesExpected.add(IdentityRecoveryConstants.ConnectorConfig.RESEND_CONFIRMATION_RECAPTCHA_ENABLE);
        propertiesExpected.add(IdentityRecoveryConstants.ConnectorConfig.SELF_REGISTRATION_AUTO_LOGIN);
        propertiesExpected.add(IdentityRecoveryConstants.ConnectorConfig.SELF_REGISTRATION_AUTO_LOGIN_ALIAS_NAME);
        propertiesExpected.add(IdentityRecoveryConstants.ConnectorConfig.ENABLE_DYNAMIC_REGISTRATION_PORTAL);

        String[] propertiesArrayExpected = propertiesExpected.toArray(new String[0]);

        String[] properties = selfRegistrationConfigImpl.getPropertyNames();

        for (int i = 0; i < propertiesArrayExpected.length; i++) {
            assertEquals(properties[i], propertiesArrayExpected[i]);
        }
    }

    @Test
    public void testGetDefaultPropertyValues() throws IdentityGovernanceException {

        String testEnableSelfSignUp = "false";
        String testEnableAccountLockOnCreation = "true";
        String testEnableSendOTPInEmail = "false";
        String testEnableEmailOTP = "false";
        String testUseUppercaseCharactersInOTP = StringUtils.EMPTY;
        String testUseLowercaseCharactersInOTP = StringUtils.EMPTY;
        String testUseNumbersInOTP = StringUtils.EMPTY;
        String testOtpLength = "6";
        String testEnableSendNotificationOnCreation = "false";
        String testShowUsernameUnavailability = "true";
        String testEnableNotificationInternallyManage = "true";
        String testEnableSelfRegistrationReCaptcha = "true";
        String testVerificationCodeExpiryTime = "1440";
        String testVerificationSMSOTPExpiryTime = "1";
        String testVerificationSMSOTPRegex = "[a-zA-Z0-9]{6}";
        String testEnableDynamicRegistrationPortal = "false";
        String selfRegistrationCallbackRegex = IdentityRecoveryConstants.DEFAULT_CALLBACK_REGEX;
        String enableSelfSignUpConfirmationNotification = "false";
        String enableResendConfirmationRecaptcha = "false";
        String enableSelfRegistrationAutoLogin = "false";
        String enableSelfRegistrationAutoLoginAlias = "wso2carbon";

        mockedIdentityUtil.when(
                        () -> IdentityUtil.getProperty(IdentityRecoveryConstants.ConnectorConfig.ENABLE_SELF_SIGNUP))
                .thenReturn(testEnableSelfSignUp);
        mockedIdentityUtil.when(
                        () -> IdentityUtil.getProperty(IdentityRecoveryConstants.ConnectorConfig.ACCOUNT_LOCK_ON_CREATION))
                .thenReturn(testEnableAccountLockOnCreation);
        mockedIdentityUtil.when(() -> IdentityUtil.getProperty(
                        IdentityRecoveryConstants.ConnectorConfig.SELF_REGISTRATION_SEND_OTP_IN_EMAIL))
                .thenReturn(testEnableSendOTPInEmail);
        mockedIdentityUtil.when(() -> IdentityUtil.getProperty(
                        IdentityRecoveryConstants.ConnectorConfig.SELF_REGISTRATION_EMAIL_OTP_ENABLE))
                .thenReturn(testEnableEmailOTP);
        mockedIdentityUtil.when(() -> IdentityUtil.getProperty(
                        IdentityRecoveryConstants.ConnectorConfig.SELF_REGISTRATION_USE_UPPERCASE_CHARACTERS_IN_OTP))
                .thenReturn(testUseUppercaseCharactersInOTP);
        mockedIdentityUtil.when(() -> IdentityUtil.getProperty(
                        IdentityRecoveryConstants.ConnectorConfig.SELF_REGISTRATION_USE_LOWERCASE_CHARACTERS_IN_OTP))
                .thenReturn(testUseLowercaseCharactersInOTP);
        mockedIdentityUtil.when(() -> IdentityUtil.getProperty(
                        IdentityRecoveryConstants.ConnectorConfig.SELF_REGISTRATION_USE_NUMBERS_IN_OTP))
                .thenReturn(testUseNumbersInOTP);
        mockedIdentityUtil.when(
                        () -> IdentityUtil.getProperty(IdentityRecoveryConstants.ConnectorConfig.SELF_REGISTRATION_OTP_LENGTH))
                .thenReturn(testOtpLength);
        mockedIdentityUtil.when(() -> IdentityUtil.getProperty(
                        IdentityRecoveryConstants.ConnectorConfig.SEND_CONFIRMATION_NOTIFICATION))
                .thenReturn(testEnableSendNotificationOnCreation);
        mockedIdentityUtil.when(
                        () -> IdentityUtil.getProperty(IdentityRecoveryConstants.ConnectorConfig.SHOW_USERNAME_UNAVAILABILITY))
                .thenReturn(testShowUsernameUnavailability);
        mockedIdentityUtil.when(() -> IdentityUtil.getProperty(
                        IdentityRecoveryConstants.ConnectorConfig.SIGN_UP_NOTIFICATION_INTERNALLY_MANAGE))
                .thenReturn(testEnableNotificationInternallyManage);
        mockedIdentityUtil.when(
                        () -> IdentityUtil.getProperty(IdentityRecoveryConstants.ConnectorConfig.SELF_REGISTRATION_RE_CAPTCHA))
                .thenReturn(testEnableSelfRegistrationReCaptcha);
        mockedIdentityUtil.when(() -> IdentityUtil.getProperty(
                        IdentityRecoveryConstants.ConnectorConfig.SELF_REGISTRATION_VERIFICATION_CODE_EXPIRY_TIME))
                .thenReturn(testVerificationCodeExpiryTime);
        mockedIdentityUtil.when(() -> IdentityUtil.getProperty(
                        IdentityRecoveryConstants.ConnectorConfig.SELF_REGISTRATION_SMSOTP_VERIFICATION_CODE_EXPIRY_TIME))
                .thenReturn(testVerificationSMSOTPExpiryTime);
        mockedIdentityUtil.when(() -> IdentityUtil.getProperty(
                        IdentityRecoveryConstants.ConnectorConfig.SELF_REGISTRATION_SMS_OTP_REGEX))
                .thenReturn(testVerificationSMSOTPRegex);
        mockedIdentityUtil.when(() -> IdentityUtil.getPropertyWithoutStandardPort(
                        IdentityRecoveryConstants.ConnectorConfig.SELF_REGISTRATION_CALLBACK_REGEX))
                .thenReturn(selfRegistrationCallbackRegex);
        mockedIdentityUtil.when(() -> IdentityUtil.getProperty(
                        IdentityRecoveryConstants.ConnectorConfig.SELF_REGISTRATION_NOTIFY_ACCOUNT_CONFIRMATION))
                .thenReturn(enableSelfSignUpConfirmationNotification);
        mockedIdentityUtil.when(() -> IdentityUtil.getProperty(
                        IdentityRecoveryConstants.ConnectorConfig.RESEND_CONFIRMATION_RECAPTCHA_ENABLE))
                .thenReturn(enableResendConfirmationRecaptcha);
        mockedIdentityUtil.when(
                        () -> IdentityUtil.getProperty(IdentityRecoveryConstants.ConnectorConfig.SELF_REGISTRATION_AUTO_LOGIN))
                .thenReturn(enableSelfRegistrationAutoLogin);
        mockedIdentityUtil.when(() -> IdentityUtil.getProperty(
                        IdentityRecoveryConstants.ConnectorConfig.SELF_REGISTRATION_AUTO_LOGIN_ALIAS_NAME))
                .thenReturn(enableSelfRegistrationAutoLoginAlias);
        mockedIdentityUtil.when(() -> IdentityUtil.getProperty(
                        IdentityRecoveryConstants.ConnectorConfig.ENABLE_DYNAMIC_REGISTRATION_PORTAL))
                .thenReturn(testEnableDynamicRegistrationPortal);

        Map<String, String> propertiesExpected = new HashMap<>();
        propertiesExpected.put(IdentityRecoveryConstants.ConnectorConfig.ENABLE_SELF_SIGNUP, testEnableSelfSignUp);
        propertiesExpected.put(IdentityRecoveryConstants.ConnectorConfig.ACCOUNT_LOCK_ON_CREATION,
                testEnableAccountLockOnCreation);
        propertiesExpected.put(IdentityRecoveryConstants.ConnectorConfig.SELF_REGISTRATION_SEND_OTP_IN_EMAIL,
                testEnableSendOTPInEmail);
        propertiesExpected.put(IdentityRecoveryConstants.ConnectorConfig.SELF_REGISTRATION_EMAIL_OTP_ENABLE,
                testEnableEmailOTP);
        propertiesExpected.put(
                IdentityRecoveryConstants.ConnectorConfig.SELF_REGISTRATION_USE_UPPERCASE_CHARACTERS_IN_OTP,
                testUseUppercaseCharactersInOTP);
        propertiesExpected.put(
                IdentityRecoveryConstants.ConnectorConfig.SELF_REGISTRATION_USE_LOWERCASE_CHARACTERS_IN_OTP,
                testUseLowercaseCharactersInOTP);
        propertiesExpected.put(IdentityRecoveryConstants.ConnectorConfig.SELF_REGISTRATION_USE_NUMBERS_IN_OTP,
                testUseNumbersInOTP);
        propertiesExpected.put(IdentityRecoveryConstants.ConnectorConfig.SELF_REGISTRATION_OTP_LENGTH,
                testOtpLength);
        propertiesExpected.put(IdentityRecoveryConstants.ConnectorConfig.SEND_CONFIRMATION_NOTIFICATION,
                testEnableSendNotificationOnCreation);
        propertiesExpected.put(IdentityRecoveryConstants.ConnectorConfig.SHOW_USERNAME_UNAVAILABILITY,
                testShowUsernameUnavailability);
        propertiesExpected.put(IdentityRecoveryConstants.ConnectorConfig.SIGN_UP_NOTIFICATION_INTERNALLY_MANAGE,
                testEnableNotificationInternallyManage);
        propertiesExpected.put(IdentityRecoveryConstants.ConnectorConfig.SELF_REGISTRATION_RE_CAPTCHA,
                testEnableSelfRegistrationReCaptcha);
        propertiesExpected.put(
                IdentityRecoveryConstants.ConnectorConfig.SELF_REGISTRATION_VERIFICATION_CODE_EXPIRY_TIME,
                testVerificationCodeExpiryTime);
        propertiesExpected.put(
                IdentityRecoveryConstants.ConnectorConfig.SELF_REGISTRATION_SMSOTP_VERIFICATION_CODE_EXPIRY_TIME,
                testVerificationSMSOTPExpiryTime);
        propertiesExpected.put(IdentityRecoveryConstants.ConnectorConfig.SELF_REGISTRATION_SMS_OTP_REGEX,
                testVerificationSMSOTPRegex);
        propertiesExpected.put(IdentityRecoveryConstants.ConnectorConfig.SELF_REGISTRATION_CALLBACK_REGEX,
                selfRegistrationCallbackRegex);
        propertiesExpected.put(IdentityRecoveryConstants.ConnectorConfig.SELF_REGISTRATION_AUTO_LOGIN,
                enableSelfRegistrationAutoLogin);
        propertiesExpected.put(IdentityRecoveryConstants.ConnectorConfig.SELF_REGISTRATION_AUTO_LOGIN_ALIAS_NAME,
                enableSelfRegistrationAutoLoginAlias);
        propertiesExpected.put(IdentityRecoveryConstants.ConnectorConfig.ENABLE_DYNAMIC_REGISTRATION_PORTAL,
                testEnableDynamicRegistrationPortal);

        try {
            propertiesExpected.put(LIST_PURPOSE_PROPERTY_KEY, CONSENT_LIST_URL + "&callback=" + (URLEncoder.encode
                    (CALLBACK_URL, StandardCharsets.UTF_8.name())));
        } catch (UnsupportedEncodingException e) {
            throw new IdentityGovernanceException("Error while encoding callback url: " + CALLBACK_URL, e);
        }
        propertiesExpected.put(IdentityRecoveryConstants.ConnectorConfig.SELF_REGISTRATION_NOTIFY_ACCOUNT_CONFIRMATION,
                enableSelfSignUpConfirmationNotification);
        propertiesExpected.put(IdentityRecoveryConstants.ConnectorConfig.RESEND_CONFIRMATION_RECAPTCHA_ENABLE,
                enableResendConfirmationRecaptcha);
        String tenantDomain = "admin";
        // Here tenantDomain parameter is not used by method itself
        Properties properties = selfRegistrationConfigImpl.getDefaultPropertyValues(tenantDomain);
        Map<String, String> defaultProperties = new HashMap<String, String>((Map) properties);

        assertEquals(defaultProperties, propertiesExpected, "Maps are not equal");
    }

    @Test
    public void testGetDefaultProperties() throws IdentityGovernanceException {

        String tenantDomain = "admin";
        String[] propertyNames = new String[]{"property1", "property2", "property3"};

        // Here tenantDomain and propertyNames parameters are not used by method itself
        Map<String, String> defaultPropertyValues = selfRegistrationConfigImpl.getDefaultPropertyValues(propertyNames,
                tenantDomain);
        assertNull(defaultPropertyValues);
    }

    @Test
    public void testGetMetadata() {

        Map<String, Property> metaDataExpected = new HashMap<>();
        metaDataExpected.put(IdentityRecoveryConstants.ConnectorConfig.ENABLE_SELF_SIGNUP,
                getPropertyObject(IdentityMgtConstants.DataTypes.BOOLEAN.getValue()));

        metaDataExpected.put(IdentityRecoveryConstants.ConnectorConfig.ACCOUNT_LOCK_ON_CREATION,
                getPropertyObject(IdentityMgtConstants.DataTypes.BOOLEAN.getValue()));

        metaDataExpected.put(IdentityRecoveryConstants.ConnectorConfig.SELF_REGISTRATION_SEND_OTP_IN_EMAIL,
                getPropertyObject(IdentityMgtConstants.DataTypes.BOOLEAN.getValue()));

        metaDataExpected.put(IdentityRecoveryConstants.ConnectorConfig.SELF_REGISTRATION_EMAIL_OTP_ENABLE,
                getPropertyObject(IdentityMgtConstants.DataTypes.BOOLEAN.getValue()));

        metaDataExpected.put(
                IdentityRecoveryConstants.ConnectorConfig.SELF_REGISTRATION_USE_UPPERCASE_CHARACTERS_IN_OTP,
                getPropertyObject(IdentityMgtConstants.DataTypes.BOOLEAN.getValue()));

        metaDataExpected.put(
                IdentityRecoveryConstants.ConnectorConfig.SELF_REGISTRATION_USE_LOWERCASE_CHARACTERS_IN_OTP,
                getPropertyObject(IdentityMgtConstants.DataTypes.BOOLEAN.getValue()));

        metaDataExpected.put(IdentityRecoveryConstants.ConnectorConfig.SELF_REGISTRATION_USE_NUMBERS_IN_OTP,
                getPropertyObject(IdentityMgtConstants.DataTypes.BOOLEAN.getValue()));

        metaDataExpected.put(IdentityRecoveryConstants.ConnectorConfig.SELF_REGISTRATION_OTP_LENGTH,
                getPropertyObject(IdentityMgtConstants.DataTypes.STRING.getValue()));

        metaDataExpected.put(IdentityRecoveryConstants.ConnectorConfig.SEND_CONFIRMATION_NOTIFICATION,
                getPropertyObject(IdentityMgtConstants.DataTypes.BOOLEAN.getValue()));

        metaDataExpected.put(IdentityRecoveryConstants.ConnectorConfig.SHOW_USERNAME_UNAVAILABILITY,
                getPropertyObject(IdentityMgtConstants.DataTypes.BOOLEAN.getValue()));

        metaDataExpected.put(IdentityRecoveryConstants.ConnectorConfig.SIGN_UP_NOTIFICATION_INTERNALLY_MANAGE,
                getPropertyObject(IdentityMgtConstants.DataTypes.BOOLEAN.getValue()));

        metaDataExpected.put(IdentityRecoveryConstants.ConnectorConfig.SELF_REGISTRATION_RE_CAPTCHA,
                getPropertyObject(IdentityMgtConstants.DataTypes.BOOLEAN.getValue()));

        metaDataExpected.put(IdentityRecoveryConstants.ConnectorConfig.SELF_REGISTRATION_VERIFICATION_CODE_EXPIRY_TIME,
                getPropertyObject(IdentityMgtConstants.DataTypes.INTEGER.getValue()));

        metaDataExpected.put(
                IdentityRecoveryConstants.ConnectorConfig.SELF_REGISTRATION_SMSOTP_VERIFICATION_CODE_EXPIRY_TIME,
                getPropertyObject(IdentityMgtConstants.DataTypes.INTEGER.getValue()));

        metaDataExpected.put(IdentityRecoveryConstants.ConnectorConfig.SELF_REGISTRATION_SMS_OTP_REGEX,
                getPropertyObject(IdentityMgtConstants.DataTypes.STRING.getValue()));

        metaDataExpected.put(IdentityRecoveryConstants.ConnectorConfig.SELF_REGISTRATION_AUTO_LOGIN,
                getPropertyObject(IdentityMgtConstants.DataTypes.BOOLEAN.getValue()));

        metaDataExpected.put(IdentityRecoveryConstants.ConnectorConfig.SELF_REGISTRATION_AUTO_LOGIN_ALIAS_NAME,
                getPropertyObject(IdentityMgtConstants.DataTypes.STRING.getValue()));

        metaDataExpected.put(IdentityRecoveryConstants.ConnectorConfig.SELF_REGISTRATION_NOTIFY_ACCOUNT_CONFIRMATION,
                getPropertyObject(IdentityMgtConstants.DataTypes.BOOLEAN.getValue()));

        metaDataExpected.put(IdentityRecoveryConstants.ConnectorConfig.RESEND_CONFIRMATION_RECAPTCHA_ENABLE,
                getPropertyObject(IdentityMgtConstants.DataTypes.BOOLEAN.getValue()));

        metaDataExpected.put(LIST_PURPOSE_PROPERTY_KEY,
                getPropertyObject(IdentityMgtConstants.DataTypes.URI.getValue()));

        metaDataExpected.put(IdentityRecoveryConstants.ConnectorConfig.SELF_REGISTRATION_CALLBACK_REGEX,
                getPropertyObject(IdentityMgtConstants.DataTypes.STRING.getValue()));
        metaDataExpected.put(IdentityRecoveryConstants.ConnectorConfig.ENABLE_DYNAMIC_REGISTRATION_PORTAL,
                getPropertyObject(IdentityMgtConstants.DataTypes.BOOLEAN.getValue()));

        Map<String, Property> metaData = selfRegistrationConfigImpl.getMetaData();
        assertEquals(metaData, metaDataExpected, "Maps are not equal");
    }
}
