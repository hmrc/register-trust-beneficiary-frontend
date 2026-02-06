/*
 * Copyright 2026 HM Revenue & Customs
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

import org.scalacheck.Arbitrary
import pages.register.beneficiaries.charityortrust._
import pages.register.beneficiaries.charityortrust.charity._

trait PageGenerators {

  implicit lazy val arbitraryCharityInternationalAddressPage: Arbitrary[CharityInternationalAddressPage] =
    Arbitrary(CharityInternationalAddressPage(0))

  implicit lazy val arbitraryCharityUKAddressPage: Arbitrary[CharityAddressUKPage] =
    Arbitrary(CharityAddressUKPage(0))

  implicit lazy val arbitraryAddressInTheUkYesNoPage: Arbitrary[AddressInTheUkYesNoPage] =
    Arbitrary(AddressInTheUkYesNoPage(0))

  implicit lazy val arbitraryAddressYesNoPage: Arbitrary[AddressYesNoPage] =
    Arbitrary(AddressYesNoPage(0))

  implicit lazy val arbitraryHowMuchIncomePage: Arbitrary[HowMuchIncomePage] =
    Arbitrary(HowMuchIncomePage(0))

  implicit lazy val arbitraryAmountDiscretionYesNoPage: Arbitrary[AmountDiscretionYesNoPage] =
    Arbitrary(AmountDiscretionYesNoPage(0))

  implicit lazy val arbitraryCharityNamePage: Arbitrary[CharityNamePage] =
    Arbitrary(CharityNamePage(0))

  implicit lazy val arbitraryCharityOrTrustPage: Arbitrary[CharityOrTrustPage.type] =
    Arbitrary(CharityOrTrustPage)

}
