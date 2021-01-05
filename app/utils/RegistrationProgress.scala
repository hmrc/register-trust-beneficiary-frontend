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

package utils

import controllers.register.beneficiaries.AnyBeneficiaries
import models.registration.pages._
import models.{ReadableUserAnswers, Status}
import pages.QuestionPage
import pages.register.beneficiaries.AddABeneficiaryPage
import play.api.libs.json.Reads
import sections.beneficiaries._
import viewmodels.addAnother._

class RegistrationProgress extends AnyBeneficiaries {

  def beneficiariesStatus(userAnswers: ReadableUserAnswers): Option[Status] = {

    if (!isAnyBeneficiaryAdded(userAnswers)) {
      None
    } else {

      val statusList: List[IsComplete] = List(
        AddingBeneficiariesIsComplete,
        IndividualBeneficiariesAreComplete,
        ClassBeneficiariesAreComplete,
        CharityBeneficiariesAreComplete,
        CompanyBeneficiariesAreComplete,
        EmploymentRelatedBeneficiariesAreComplete,
        TrustBeneficiariesAreComplete,
        CompanyBeneficiariesAreComplete,
        OtherBeneficiariesAreComplete
      )

      statusList match {
        case Nil => None
        case list =>

          val complete = list.forall(isComplete => isComplete(userAnswers))

          Some(if (complete) {
            Status.Completed
          } else {
            Status.InProgress
          })
      }
    }
  }

  sealed trait IsComplete {
    def apply(userAnswers: ReadableUserAnswers): Boolean
  }

  sealed class ListIsComplete[T <: ViewModel](section: QuestionPage[List[T]])
                                             (implicit reads: Reads[T]) extends IsComplete {

    override def apply(userAnswers: ReadableUserAnswers): Boolean = {
      userAnswers.get(section) match {
        case Some(beneficiaries) => !beneficiaries.exists(_.status == Status.InProgress)
        case _ => true
      }
    }
  }

  private object AddingBeneficiariesIsComplete extends IsComplete {
    override def apply(userAnswers: ReadableUserAnswers): Boolean =
      userAnswers.get(AddABeneficiaryPage).contains(AddABeneficiary.NoComplete)
  }

  private object IndividualBeneficiariesAreComplete extends ListIsComplete(IndividualBeneficiaries)

  private object ClassBeneficiariesAreComplete extends ListIsComplete(ClassOfBeneficiaries)

  private object CharityBeneficiariesAreComplete extends ListIsComplete(CharityBeneficiaries)

  private object TrustBeneficiariesAreComplete extends ListIsComplete(TrustBeneficiaries)

  private object CompanyBeneficiariesAreComplete extends ListIsComplete(CompanyBeneficiaries)

  private object EmploymentRelatedBeneficiariesAreComplete extends ListIsComplete(LargeBeneficiaries)

  private object OtherBeneficiariesAreComplete extends ListIsComplete(OtherBeneficiaries)
}
