/*
 * Copyright 2021 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package mapping.reads

import base.SpecBase
import models.YesNoDontKnow
import models.core.pages.FullName
import org.scalatest.{MustMatchers, OptionValues}
import play.api.libs.json.Json

class IndividualBeneficiarySpec extends SpecBase with MustMatchers with OptionValues {

  "IndividualBeneficiary reads" must {

    "parse the old mental capacity question" in {
      val json = Json.parse(
        """
          |{
          | "name": {
          |   "firstName": "John",
          |   "lastName": "Smith"
          | },
          | "mentalCapacityYesNo": true
          |}
          |""".stripMargin)

      json.as[IndividualBeneficiary] mustBe IndividualBeneficiary(
        name = FullName("John", None, "Smith"),
        roleInCompany = None,
        dateOfBirth = None,
        nationalInsuranceNumber = None,
        passportDetails = None,
        idCardDetails = None,
        ukAddress = None,
        internationalAddress = None,
        vulnerableYesNo = None,
        income = None,
        incomeYesNo = None,
        countryOfResidence = None,
        countryOfNationality = None,
        mentalCapacityYesNo = Some(YesNoDontKnow.Yes)
      )
    }

    "parse the new mental capacity question" in {
      val json = Json.parse(
        """
          |{
          | "name": {
          |   "firstName": "John",
          |   "lastName": "Smith"
          | },
          | "mentalCapacityYesNo": "dontKnow"
          |}
          |""".stripMargin)

      json.as[IndividualBeneficiary] mustBe IndividualBeneficiary(
        name = FullName("John", None, "Smith"),
        roleInCompany = None,
        dateOfBirth = None,
        nationalInsuranceNumber = None,
        passportDetails = None,
        idCardDetails = None,
        ukAddress = None,
        internationalAddress = None,
        vulnerableYesNo = None,
        income = None,
        incomeYesNo = None,
        countryOfResidence = None,
        countryOfNationality = None,
        mentalCapacityYesNo = Some(YesNoDontKnow.DontKnow)
      )
    }

  }

}
