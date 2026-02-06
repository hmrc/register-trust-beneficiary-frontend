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

package models

import models.registration.pages.WhatTypeOfBeneficiary
import utils.Constants.MAX
import viewmodels.RadioOption
import viewmodels.addAnother._

case class Beneficiaries(
  individuals: List[IndividualBeneficiaryViewModel] = Nil,
  unidentified: List[ClassOfBeneficiaryViewModel] = Nil,
  charities: List[CharityBeneficiaryViewModel] = Nil,
  trusts: List[TrustBeneficiaryViewModel] = Nil,
  companies: List[CompanyBeneficiaryViewModel] = Nil,
  large: List[EmploymentRelatedBeneficiaryViewModel] = Nil,
  other: List[OtherBeneficiaryViewModel] = Nil
) {

  type BeneficiaryOption  = (Int, WhatTypeOfBeneficiary)
  type BeneficiaryOptions = List[BeneficiaryOption]

  private val options: BeneficiaryOptions =
    (individuals.size, WhatTypeOfBeneficiary.Individual) ::
      (unidentified.size, WhatTypeOfBeneficiary.ClassOfBeneficiary) ::
      (charities.size, WhatTypeOfBeneficiary.Charity) ::
      (trusts.size, WhatTypeOfBeneficiary.Trust) ::
      (companies.size, WhatTypeOfBeneficiary.Company) ::
      (large.size, WhatTypeOfBeneficiary.Employment) ::
      (other.size, WhatTypeOfBeneficiary.Other) ::
      Nil

  val nonMaxedOutOptions: List[RadioOption] = {

    /** Determines which Radio Options to display.
      *
      *  A beneficiary type is considered 'maxed-out' if there are 25 or more instances of it.
      *  This recursive function begins with a list of 'uncombined' and 'non-maxed-out' beneficiary types.
      *  In the case of Charity and Trust, and Company and EmploymentRelated, it will combine the two if neither are 'maxed-out'.
      *  This is so that no radio option is displayed that corresponds to a beneficiary type that is 'maxed-out'.
      */
    def combineOptions(uncombinedOptions: BeneficiaryOptions): BeneficiaryOptions = {
      @scala.annotation.tailrec
      def recurse(uncombinedOptions: BeneficiaryOptions, combinedOptions: BeneficiaryOptions): BeneficiaryOptions =
        uncombinedOptions match {
          case Nil => combinedOptions
          case List(head, next, _*)
              if head._2 == WhatTypeOfBeneficiary.Charity && next._2 == WhatTypeOfBeneficiary.Trust =>
            val combinedOption: BeneficiaryOption = (head._1 + next._1, WhatTypeOfBeneficiary.CharityOrTrust)
            recurse(uncombinedOptions.tail.tail, combinedOptions :+ combinedOption)
          case List(head, next, _*)
              if head._2 == WhatTypeOfBeneficiary.Company && next._2 == WhatTypeOfBeneficiary.Employment =>
            val combinedOption: BeneficiaryOption = (head._1 + next._1, WhatTypeOfBeneficiary.CompanyOrEmployment)
            recurse(uncombinedOptions.tail.tail, combinedOptions :+ combinedOption)
          case _   =>
            recurse(uncombinedOptions.tail, combinedOptions :+ uncombinedOptions.head)
        }
      recurse(uncombinedOptions, Nil)
    }

    combineOptions(options.filter(x => x._1 < MAX)).map { x =>
      RadioOption(WhatTypeOfBeneficiary.prefix, x._2.toString)
    }
  }

  val maxedOutOptions: List[RadioOption] =
    options.filter(x => x._1 >= MAX).map { x =>
      RadioOption(WhatTypeOfBeneficiary.prefix, x._2.toString)
    }

}
