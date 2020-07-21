/*
 * Copyright 2020 HM Revenue & Customs
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

package generators

import models.core.pages.{FullName, InternationalAddress, UKAddress}
import models.registration.pages.CharityOrTrust
import org.scalacheck.Arbitrary
import org.scalacheck.Arbitrary.arbitrary
import pages.register.beneficiaries.charityOrTrust._
import play.api.libs.json.{JsValue, Json}

trait UserAnswersEntryGenerators extends PageGenerators with ModelGenerators {

  implicit lazy val arbitraryCharityInternationalAddressAnswersEntry: Arbitrary[(CharityInternationalAddressPage, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[CharityInternationalAddressPage]
        value <- arbitrary[InternationalAddress].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryCharityAddressUKAnswersEntry: Arbitrary[(CharityAddressUKPage, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[CharityAddressUKPage]
        value <- arbitrary[UKAddress].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryAddressInTheUkYesNoAnswersEntry: Arbitrary[(AddressInTheUkYesNoPage, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[AddressInTheUkYesNoPage]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryAddressYesNoAnswersEntry: Arbitrary[(AddressYesNoPage, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[AddressYesNoPage]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryHowMuchIncomeAnswersEntry: Arbitrary[(HowMuchIncomePage, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[HowMuchIncomePage]
        value <- arbitrary[String].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryAmountDiscretionYesNoAnswersEntry: Arbitrary[(AmountDiscretionYesNoPage, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[AmountDiscretionYesNoPage]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryCharityNameUserAnswersEntry: Arbitrary[(CharityNamePage, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[CharityNamePage]
        value <- arbitrary[FullName].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryCharityOrTrustUserAnswersEntry: Arbitrary[(CharityOrTrustPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[CharityOrTrustPage.type]
        value <- arbitrary[CharityOrTrust].map(Json.toJson(_))
      } yield (page, value)
    }
}
