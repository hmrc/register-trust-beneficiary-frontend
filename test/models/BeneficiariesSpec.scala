/*
 * Copyright 2022 HM Revenue & Customs
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

package models

import base.SpecBase
import models.Status._
import models.core.pages.FullName
import models.registration.pages.WhatTypeOfBeneficiary
import viewmodels.RadioOption
import viewmodels.addAnother._

class BeneficiariesSpec extends SpecBase {

  val fullName: FullName = FullName("Full", None, "Name")
  val name: String = "Name"

  val max: Int = 25

  val individual: IndividualBeneficiaryViewModel = IndividualBeneficiaryViewModel(Some(fullName.toString), Completed)
  val unidentified: ClassOfBeneficiaryViewModel = ClassOfBeneficiaryViewModel(Some(name), Completed)
  val charity: CharityBeneficiaryViewModel = CharityBeneficiaryViewModel(Some(name), Completed)
  val trust: TrustBeneficiaryViewModel = TrustBeneficiaryViewModel(Some(name), Completed)
  val company: CompanyBeneficiaryViewModel = CompanyBeneficiaryViewModel(Some(name), Completed)
  val large: EmploymentRelatedBeneficiaryViewModel = EmploymentRelatedBeneficiaryViewModel(Some(name), Completed)
  val other: OtherBeneficiaryViewModel = OtherBeneficiaryViewModel(Some(name), Completed)

  val prefix: String = WhatTypeOfBeneficiary.prefix

  "Beneficiaries model" must {

    "determine the non-maxed-out options" when {

      "individual maxed out" in {
        val beneficiaries = Beneficiaries(
          individuals = List.fill(max)(individual)
        )

        beneficiaries.nonMaxedOutOptions mustBe List(
          RadioOption(prefix, WhatTypeOfBeneficiary.ClassOfBeneficiary.toString),
          RadioOption(prefix, WhatTypeOfBeneficiary.CharityOrTrust.toString),
          RadioOption(prefix, WhatTypeOfBeneficiary.CompanyOrEmployment.toString),
          RadioOption(prefix, WhatTypeOfBeneficiary.Other.toString)
        )
      }

      "unidentified maxed out" in {
        val beneficiaries = Beneficiaries(
          unidentified = List.fill(max)(unidentified)
        )

        beneficiaries.nonMaxedOutOptions mustBe List(
          RadioOption(prefix, WhatTypeOfBeneficiary.Individual.toString),
          RadioOption(prefix, WhatTypeOfBeneficiary.CharityOrTrust.toString),
          RadioOption(prefix, WhatTypeOfBeneficiary.CompanyOrEmployment.toString),
          RadioOption(prefix, WhatTypeOfBeneficiary.Other.toString)
        )
      }

      "charity maxed out" in {
        val beneficiaries = Beneficiaries(
          charities = List.fill(max)(charity)
        )

        beneficiaries.nonMaxedOutOptions mustBe List(
          RadioOption(prefix, WhatTypeOfBeneficiary.Individual.toString),
          RadioOption(prefix, WhatTypeOfBeneficiary.ClassOfBeneficiary.toString),
          RadioOption(prefix, WhatTypeOfBeneficiary.Trust.toString),
          RadioOption(prefix, WhatTypeOfBeneficiary.CompanyOrEmployment.toString),
          RadioOption(prefix, WhatTypeOfBeneficiary.Other.toString)
        )
      }

      "trust maxed out" in {
        val beneficiaries = Beneficiaries(
          trusts = List.fill(max)(trust)
        )

        beneficiaries.nonMaxedOutOptions mustBe List(
          RadioOption(prefix, WhatTypeOfBeneficiary.Individual.toString),
          RadioOption(prefix, WhatTypeOfBeneficiary.ClassOfBeneficiary.toString),
          RadioOption(prefix, WhatTypeOfBeneficiary.Charity.toString),
          RadioOption(prefix, WhatTypeOfBeneficiary.CompanyOrEmployment.toString),
          RadioOption(prefix, WhatTypeOfBeneficiary.Other.toString)
        )
      }

      "charity and trust maxed out" in {
        val beneficiaries = Beneficiaries(
          charities = List.fill(max)(charity),
          trusts = List.fill(max)(trust)
        )

        beneficiaries.nonMaxedOutOptions mustBe List(
          RadioOption(prefix, WhatTypeOfBeneficiary.Individual.toString),
          RadioOption(prefix, WhatTypeOfBeneficiary.ClassOfBeneficiary.toString),
          RadioOption(prefix, WhatTypeOfBeneficiary.CompanyOrEmployment.toString),
          RadioOption(prefix, WhatTypeOfBeneficiary.Other.toString)
        )
      }

      "company maxed out" in {
        val beneficiaries = Beneficiaries(
          companies = List.fill(max)(company)
        )

        beneficiaries.nonMaxedOutOptions mustBe List(
          RadioOption(prefix, WhatTypeOfBeneficiary.Individual.toString),
          RadioOption(prefix, WhatTypeOfBeneficiary.ClassOfBeneficiary.toString),
          RadioOption(prefix, WhatTypeOfBeneficiary.CharityOrTrust.toString),
          RadioOption(prefix, WhatTypeOfBeneficiary.Employment.toString),
          RadioOption(prefix, WhatTypeOfBeneficiary.Other.toString)
        )
      }

      "employment maxed out" in {
        val beneficiaries = Beneficiaries(
          large = List.fill(max)(large)
        )

        beneficiaries.nonMaxedOutOptions mustBe List(
          RadioOption(prefix, WhatTypeOfBeneficiary.Individual.toString),
          RadioOption(prefix, WhatTypeOfBeneficiary.ClassOfBeneficiary.toString),
          RadioOption(prefix, WhatTypeOfBeneficiary.CharityOrTrust.toString),
          RadioOption(prefix, WhatTypeOfBeneficiary.Company.toString),
          RadioOption(prefix, WhatTypeOfBeneficiary.Other.toString)
        )
      }

      "company and employment maxed out" in {
        val beneficiaries = Beneficiaries(
          companies = List.fill(max)(company),
          large = List.fill(max)(large)
        )

        beneficiaries.nonMaxedOutOptions mustBe List(
          RadioOption(prefix, WhatTypeOfBeneficiary.Individual.toString),
          RadioOption(prefix, WhatTypeOfBeneficiary.ClassOfBeneficiary.toString),
          RadioOption(prefix, WhatTypeOfBeneficiary.CharityOrTrust.toString),
          RadioOption(prefix, WhatTypeOfBeneficiary.Other.toString)
        )
      }

      "other maxed out" in {
        val beneficiaries = Beneficiaries(
          other = List.fill(max)(other)
        )

        beneficiaries.nonMaxedOutOptions mustBe List(
          RadioOption(prefix, WhatTypeOfBeneficiary.Individual.toString),
          RadioOption(prefix, WhatTypeOfBeneficiary.ClassOfBeneficiary.toString),
          RadioOption(prefix, WhatTypeOfBeneficiary.CharityOrTrust.toString),
          RadioOption(prefix, WhatTypeOfBeneficiary.CompanyOrEmployment.toString)
        )
      }

      "all maxed out" in {
        val beneficiaries = Beneficiaries(
          individuals = List.fill(max)(individual),
          unidentified = List.fill(max)(unidentified),
          charities = List.fill(max)(charity),
          trusts = List.fill(max)(trust),
          companies = List.fill(max)(company),
          large = List.fill(max)(large),
          other = List.fill(max)(other)
        )

        beneficiaries.nonMaxedOutOptions mustBe Nil
      }
    }
  }
}
