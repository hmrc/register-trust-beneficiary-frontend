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
import models.registration.pages._
import models.{Status, UserAnswers}
import pages.register.beneficiaries.AddABeneficiaryPage
import sections.beneficiaries.{ClassOfBeneficiaries, IndividualBeneficiaries}

class RegistrationProgress @Inject()() {

  private def determineStatus(complete: Boolean): Option[Status] = {
    if (complete) {
      Some(Status.Completed)
    } else {
      Some(Status.InProgress)
    }
  }

  def beneficiariesStatus(userAnswers: UserAnswers): Option[Status] = {

    def individualBeneficiariesComplete: Boolean = {
      val individuals = userAnswers.get(IndividualBeneficiaries).getOrElse(List.empty)

      if (individuals.isEmpty) {
        false
      } else {
        !individuals.exists(_.status == Status.InProgress)
      }
    }

    def classComplete: Boolean = {
      val classes = userAnswers.get(ClassOfBeneficiaries).getOrElse(List.empty)
      if (classes.isEmpty) {
        false
      } else {
        !classes.exists(_.status == Status.InProgress)
      }
    }

    val noMoreToAdd = userAnswers.get(AddABeneficiaryPage).contains(AddABeneficiary.NoComplete)

    val individuals = userAnswers.get(IndividualBeneficiaries).getOrElse(List.empty)
    val classes = userAnswers.get(ClassOfBeneficiaries).getOrElse(List.empty)

    (individuals, classes) match {
      case (Nil, Nil) => None
      case (_, Nil) =>
        determineStatus(individualBeneficiariesComplete && noMoreToAdd)
      case (Nil, _) =>
        determineStatus(classComplete && noMoreToAdd)
      case (_, _) =>
        determineStatus(individualBeneficiariesComplete && classComplete && noMoreToAdd)
    }
  }
}
