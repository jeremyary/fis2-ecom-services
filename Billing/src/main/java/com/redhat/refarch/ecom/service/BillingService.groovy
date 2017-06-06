/**
 *  Copyright 2005-2017 Red Hat, Inc.
 *
 *  Red Hat licenses this file to you under the Apache License, version
 *  2.0 (the "License") you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 *  implied.  See the License for the specific language governing
 *  permissions and limitations under the License.
 */
package com.redhat.refarch.ecom.service

import com.redhat.refarch.ecom.model.Result
import com.redhat.refarch.ecom.model.Result.Status
import com.redhat.refarch.ecom.model.Transaction
import org.apache.camel.Consume
import org.springframework.stereotype.Component

import java.util.logging.Level
import java.util.logging.Logger

@Component
class BillingService {

    Logger logger = Logger.getLogger(getClass().getName())

    @Consume(uri = "amq:billing.process")
    static Result process(Transaction transaction) {

        Result result = new Result(transaction.getCustomerName(), transaction.getOrderNumber(), transaction.getCustomerId())

        Calendar now = Calendar.getInstance()
        Calendar calendar = Calendar.getInstance()
        calendar.clear()
        calendar.set(transaction.getExpYear(), transaction.getExpMonth(), 1)

        result.setStatus(calendar.after(now) ? Status.SUCCESS : Status.FAILURE)
        return result
    }

    @Consume(uri = "amq:billing.refund")
    void refund(int transactionNumber) {
        logInfo("Asked to refund credit card transaction: " + transactionNumber)
    }

    void logInfo(String message) {
        logger.log(Level.INFO, message)
    }
}