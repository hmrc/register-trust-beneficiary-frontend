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

package models

import mapping.registration._
import models.registration.pages.WhatTypeOfBeneficiary
import play.api.i18n.{Messages, MessagesProvider}
import play.api.libs.json.{Format, Json}
import viewmodels.RadioOption

case class BeneficiaryType(individualDetails: Option[List[IndividualDetailsType]],
                           company: Option[List[CompanyType]],
                           trust: Option[List[BeneficiaryTrustType]],
                           charity: Option[List[CharityType]],
                           unidentified: Option[List[UnidentifiedType]],
                           large: Option[List[LargeType]],
                           other: Option[List[OtherType]]) {

  type BeneficiaryOption = (Int, WhatTypeOfBeneficiary)
  type BeneficiaryOptions = List[BeneficiaryOption]

  def addToHeading()(implicit mp: MessagesProvider): String =
    (individualDetails ++ unidentified ++ company ++ large ++ trust ++ charity ++ other).size match {
      case 0 => Messages("addABeneficiary.heading")
      case 1 => Messages("addABeneficiary.singular.heading")
      case l => Messages("addABeneficiary.count.heading", l)
    }

  private val options: BeneficiaryOptions = {
    (individualDetails.size, WhatTypeOfBeneficiary.Individual) ::
      (unidentified.size, WhatTypeOfBeneficiary.ClassOfBeneficiary) ::
      (charity.size, WhatTypeOfBeneficiary.Charity) ::
      (trust.size, WhatTypeOfBeneficiary.Trust) ::
      (company.size, WhatTypeOfBeneficiary.Company) ::
      (large.size, WhatTypeOfBeneficiary.EmploymentRelated) ::
      (other.size, WhatTypeOfBeneficiary.Other) ::
      Nil
  }

  val nonMaxedOutOptions: List[RadioOption] = {

    def combineOptions(uncombinedOptions: BeneficiaryOptions): BeneficiaryOptions = {
      @scala.annotation.tailrec
      def recurse(uncombinedOptions: BeneficiaryOptions, combinedOptions: BeneficiaryOptions): BeneficiaryOptions = {
        uncombinedOptions match {
          case Nil => combinedOptions
          case List(head, next, _*) if head._2 == WhatTypeOfBeneficiary.Charity && next._2 == WhatTypeOfBeneficiary.Trust =>
            val combinedOption: BeneficiaryOption = (head._1 + next._1, WhatTypeOfBeneficiary.CharityOrTrust)
            recurse(uncombinedOptions.tail.tail, combinedOptions :+ combinedOption)
          case List(head, next, _*) if head._2 == WhatTypeOfBeneficiary.Company && next._2 == WhatTypeOfBeneficiary.EmploymentRelated =>
            val combinedOption: BeneficiaryOption = (head._1 + next._1, WhatTypeOfBeneficiary.CompanyOrEmployment)
            recurse(uncombinedOptions.tail.tail, combinedOptions :+ combinedOption)
          case _ =>
            recurse(uncombinedOptions.tail, combinedOptions :+ uncombinedOptions.head)
        }
      }
      recurse(uncombinedOptions, Nil)
    }

    combineOptions(options.filter(x => x._1 < 25)).map {
      x => RadioOption(WhatTypeOfBeneficiary.prefix, x._2.toString)
    }
  }

  val maxedOutOptions: List[RadioOption] = {

    options.filter(x => x._1 >= 25).map {
      x => RadioOption(WhatTypeOfBeneficiary.prefix, x._2.toString)
    }
  }

}

object BeneficiaryType {
  implicit val formats: Format[BeneficiaryType] = Json.format[BeneficiaryType]
}
