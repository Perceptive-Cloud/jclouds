/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jclouds.aws.s3.filters;

import org.jclouds.http.HttpRequest;
import org.jclouds.s3.filters.Aws4SignerForAuthorizationHeader;
import org.jclouds.s3.filters.Aws4SignerForQueryString;
import org.jclouds.s3.filters.RequestAuthorizeSignatureV4;

import javax.inject.Inject;
import javax.inject.Singleton;

import static org.jclouds.http.utils.Queries.queryParser;
import static org.jclouds.s3.filters.AwsSignatureV4Constants.AMZ_SIGNATURE_PARAM;

/**
 * Signs the AWS S3 request, supporting temporary signatures.
 */
@Singleton
public class AWSRequestAuthorizeSignatureV4 extends RequestAuthorizeSignatureV4 {

    @Inject
    public AWSRequestAuthorizeSignatureV4(Aws4SignerForAuthorizationHeader signerForAuthorizationHeader,
            Aws4SignerForQueryString signerForQueryString) {
        super(signerForAuthorizationHeader, signerForQueryString);
    }

    @Override
    protected HttpRequest signForAuthorizationHeader(HttpRequest request) {
       /*
        * Only add the Authorization header if the query string doesn't already contain
        * the 'X-Amz-Signature' parameter, otherwise S3 will fail the request complaining about
        * duplicate authentication methods. The 'Signature' parameter will be added for signed URLs
        * with expiration.
        */

        if (queryParser().apply(request.getEndpoint().getQuery()).containsKey(AMZ_SIGNATURE_PARAM)) {
            return request;
        }
        return super.signForAuthorizationHeader(request);
    }
}
