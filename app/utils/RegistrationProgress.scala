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

  sealed trait ListIsComplete extends IsComplete {
    def apply[T <: ViewModel](userAnswers: ReadableUserAnswers, section: QuestionPage[List[T]])
                             (implicit reads: Reads[List[T]]): Boolean = {

      userAnswers.get(section) match {
        case Some(beneficiaries) => !beneficiaries.exists(_.status == Status.InProgress)
        case _ => true
      }
    }
  }

  object AddingBeneficiariesIsComplete extends IsComplete {
    override def apply(userAnswers: ReadableUserAnswers): Boolean =
      userAnswers.get(AddABeneficiaryPage).contains(AddABeneficiary.NoComplete)
  }

  object IndividualBeneficiariesAreComplete extends ListIsComplete {
    override def apply(userAnswers: ReadableUserAnswers): Boolean =
      apply(userAnswers, IndividualBeneficiaries)
  }

  object ClassBeneficiariesAreComplete extends ListIsComplete {
    override def apply(userAnswers: ReadableUserAnswers): Boolean =
      apply(userAnswers, ClassOfBeneficiaries)
  }

  object CharityBeneficiariesAreComplete extends ListIsComplete {
    override def apply(userAnswers: ReadableUserAnswers): Boolean =
      apply(userAnswers, CharityBeneficiaries)
  }

  object TrustBeneficiariesAreComplete extends ListIsComplete {
    override def apply(userAnswers: ReadableUserAnswers): Boolean =
      apply(userAnswers, TrustBeneficiaries)
  }

  object CompanyBeneficiariesAreComplete extends ListIsComplete {
    override def apply(userAnswers: ReadableUserAnswers): Boolean =
      apply(userAnswers, CompanyBeneficiaries)
  }

  object OtherBeneficiariesAreComplete extends ListIsComplete {
    override def apply(userAnswers: ReadableUserAnswers): Boolean =
      apply(userAnswers, OtherBeneficiaries)
  }
}
