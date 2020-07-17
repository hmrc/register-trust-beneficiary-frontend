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

import java.time.LocalDate

import models.core.pages.{FullName, UKAddress}
import models.registration.pages.{AddABeneficiary, PassportOrIdCardDetails, WhatTypeOfBeneficiary}
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.{Arbitrary, Gen}

trait ModelGenerators {

  implicit lazy val arbitraryLocalDate : Arbitrary[LocalDate] =
    Arbitrary {
      Gen.const(LocalDate.of(2010, 10, 10))
    }

  implicit lazy val arbitraryWhatTypeOfBeneficiary: Arbitrary[WhatTypeOfBeneficiary] =
    Arbitrary {
      Gen.oneOf(WhatTypeOfBeneficiary.values)
    }

  implicit lazy val arbitraryAddABeneficiary: Arbitrary[AddABeneficiary] =
    Arbitrary {
      Gen.oneOf(AddABeneficiary.values)
    }

  implicit lazy val arbitraryUkAddress: Arbitrary[UKAddress] =
    Arbitrary {
      for {
        line1 <- arbitrary[String]
        line2 <- arbitrary[String]
        line3 <- arbitrary[String]
        line4 <- arbitrary[String]
        postcode <- arbitrary[String]
      } yield UKAddress(line1, line2, Some(line3), Some(line4), postcode)
    }

  implicit lazy val arbitraryPassportOrIdCardDetails: Arbitrary[PassportOrIdCardDetails] =
    Arbitrary {
      for {
        country <- arbitrary[String]
        cardNumber <- arbitrary[String]
        expiryDate <- arbitrary[LocalDate]
      } yield PassportOrIdCardDetails(country, cardNumber, expiryDate)
    }

  implicit lazy val arbitraryFullName : Arbitrary[FullName] = {
    Arbitrary {
      for {
        str <- arbitrary[String]
      } yield {
        FullName(str, Some(str), str)
      }
    }
  }



}
