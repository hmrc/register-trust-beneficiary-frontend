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
import javax.inject.Inject
import models.registration.pages._
import models.{ReadableUserAnswers, Status}
import pages.register.beneficiaries.AddABeneficiaryPage
import sections.beneficiaries.{CharityBeneficiaries, ClassOfBeneficiaries, CompanyBeneficiaries, IndividualBeneficiaries, TrustBeneficiaries}

class RegistrationProgress @Inject()() extends AnyBeneficiaries {

  def beneficiariesStatus(userAnswers: ReadableUserAnswers): Option[Status] = {

    if (!isAnyBeneficiaryAdded(userAnswers)) {
      None
    } else {

      val statusList: List[IsComplete] = List(
        AddingBeneficiariesIsComplete,
        IndividualBeneficiariesAreComplete,
        ClassBeneficiariesAreComplete,
        CompanyBeneficiariesAreComplete,
        TrustBeneficiariesAreComplete,
        CharityBeneficiariesAreComplete
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

  trait IsComplete {
    def apply(userAnswers: ReadableUserAnswers): Boolean
  }

  object AddingBeneficiariesIsComplete extends IsComplete {

    def apply(userAnswers: ReadableUserAnswers): Boolean = {
      userAnswers.get(AddABeneficiaryPage).contains(AddABeneficiary.NoComplete)
    }
  }

  object IndividualBeneficiariesAreComplete extends IsComplete {

    def apply(userAnswers: ReadableUserAnswers): Boolean = {

      userAnswers.get(IndividualBeneficiaries) match {
        case Some(beneficiaries@_ :: _) => !beneficiaries.exists(_.status == Status.InProgress)
        case _ => true
      }
    }
  }

  object ClassBeneficiariesAreComplete extends IsComplete {

    def apply(userAnswers: ReadableUserAnswers): Boolean = {
      userAnswers.get(ClassOfBeneficiaries) match {
        case Some(beneficiaries@_ :: _) => !beneficiaries.exists(_.status == Status.InProgress)
        case _ => true
      }
    }
  }

  object CompanyBeneficiariesAreComplete extends IsComplete {

    def apply(userAnswers: ReadableUserAnswers): Boolean = {
      userAnswers.get(CompanyBeneficiaries) match {
        case Some(beneficiaries@_ :: _) => !beneficiaries.exists(_.status == Status.InProgress)
        case _ => true
      }
    }
  }

  object TrustBeneficiariesAreComplete extends IsComplete {

    def apply(userAnswers: ReadableUserAnswers): Boolean = {
      userAnswers.get(TrustBeneficiaries) match {
        case Some(beneficiaries@_ :: _) => !beneficiaries.exists(_.status == Status.InProgress)
        case _ => true
      }
    }
  }

  object CharityBeneficiariesAreComplete extends IsComplete {

    def apply(userAnswers: ReadableUserAnswers): Boolean = {
      userAnswers.get(CharityBeneficiaries) match {
        case Some(beneficiaries@_ :: _) => !beneficiaries.exists(_.status == Status.InProgress)
        case _ => true
      }
    }
  }
}
