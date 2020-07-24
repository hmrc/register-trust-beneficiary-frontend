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

import javax.inject.Inject
import models.Status.{Completed, InProgress}
import models.registration.pages._
import models.{ReadableUserAnswers, Status}
import pages.register.beneficiaries.AddABeneficiaryPage
import sections.beneficiaries.{ClassOfBeneficiaries, IndividualBeneficiaries}
import viewmodels.addAnother.{ClassOfBeneficiaryViewModel, IndividualBeneficiaryViewModel}

class RegistrationProgress @Inject()() {

  private def determineStatus(complete: Boolean): Status = {
    if (complete) {
      Status.Completed
    } else {
      Status.InProgress
    }
  }

  def beneficiariesStatus(userAnswers: ReadableUserAnswers): Option[Status] = {

    val statusList: List[IsComplete] = List(
      userAnswers.get(IndividualBeneficiaries) map IndividualBeneficiariesStatus,
      userAnswers.get(ClassOfBeneficiaries) map ClassStatus
    ).flatten

    statusList map (_.isComplete(userAnswers))

    statusList match {
      case Nil => None
      case list =>

        list.foldLeft[Option[Status]](None){ (x, y) =>
          y.isComplete(userAnswers) match {
            case Some(false) =>
              Some(InProgress)
            case Some(true) =>
              x orElse Some(Completed)
            case _ =>
              None

          }
        }
    }

  }

  trait IsComplete {

    def isComplete(userAnswers: ReadableUserAnswers): Option[Boolean]

    def status(userAnswers: ReadableUserAnswers): Option[Status] = {

      val noMoreToAdd = userAnswers.get(AddABeneficiaryPage).contains(AddABeneficiary.NoComplete)

      isComplete(userAnswers) map { isComplete =>
        determineStatus(isComplete && noMoreToAdd)
      }

    }
  }

  case class IndividualBeneficiariesStatus(list: List[IndividualBeneficiaryViewModel]) extends IsComplete {

    def isComplete(userAnswers: ReadableUserAnswers): Option[Boolean] = {

      userAnswers.get(IndividualBeneficiaries) match {
        case Some(individuals@_::_) => Some(!individuals.exists(_.status == Status.InProgress))
        case _ => None
      }

    }
  }

  case class ClassStatus(list: List[ClassOfBeneficiaryViewModel]) extends IsComplete {

    def isComplete(userAnswers: ReadableUserAnswers): Option[Boolean] = {
      userAnswers.get(ClassOfBeneficiaries) match {
        case Some(classes@_::_) => Some(!classes.exists(_.status == Status.InProgress))
        case _ => None
      }
    }

  }

}
