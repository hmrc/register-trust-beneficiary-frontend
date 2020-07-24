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

import base.SpecBase
import controllers.routes
import models.Status.{Completed, InProgress}
import models.UserAnswers
import pages.entitystatus.CharityBeneficiaryStatus
import pages.register.beneficiaries.charityortrust.charity._
import viewmodels.{AddRow, AddToRows}

class AddABeneficiaryViewHelperSpec extends SpecBase {

  private lazy val featureUnavailableUrl: String = routes.FeatureNotAvailableController.onPageLoad().url

  "Add a beneficiary view helper" when {

    def helper(userAnswers: UserAnswers) = new AddABeneficiaryViewHelper(userAnswers, fakeDraftId)

    "charity beneficiary" must {

      val name: String = "Name"
      val label: String = "Charity"
      val default: String = "No name added"

      "render a complete charity beneficiary" in {

        val index = 0

        val userAnswers = emptyUserAnswers
          .set(CharityNamePage(0), name).success.value
          .set(AmountDiscretionYesNoPage(0), true).success.value
          .set(AddressYesNoPage(0), false).success.value
          .set(CharityBeneficiaryStatus(0), Completed).success.value

        helper(userAnswers).rows mustEqual AddToRows(
          inProgress = Nil,
          complete = List(
            AddRow(
              name = name,
              typeLabel = label,
              changeUrl = controllers.register.beneficiaries.charityOrTrust.charity.routes.CharityAnswersController.onPageLoad(index, draftId).url,
              removeUrl = featureUnavailableUrl
            )
          )
        )
      }

      "render an in progress charity beneficiary" when {

        "it has a name" in {

          val index = 0

          val userAnswers = emptyUserAnswers
            .set(CharityNamePage(0), name).success.value
            .set(CharityBeneficiaryStatus(0), InProgress).success.value

          helper(userAnswers).rows mustEqual AddToRows(
            inProgress = List(
              AddRow(
                name = name,
                typeLabel = label,
                changeUrl = controllers.register.beneficiaries.charityOrTrust.charity.routes.CharityAnswersController.onPageLoad(index, draftId).url,
                removeUrl = featureUnavailableUrl
              )
            ),
            complete = Nil
          )
        }

        "it has no name" in {

          val index = 0

          val userAnswers = emptyUserAnswers
            .set(CharityBeneficiaryStatus(0), InProgress).success.value

          helper(userAnswers).rows mustEqual AddToRows(
            inProgress = List(
              AddRow(
                name = default,
                typeLabel = label,
                changeUrl = controllers.register.beneficiaries.charityOrTrust.charity.routes.CharityAnswersController.onPageLoad(index, draftId).url,
                removeUrl = featureUnavailableUrl
              )
            ),
            complete = Nil
          )
        }
      }

      "render multiple charity beneficiaries" in {

        val name1 = "Name 1"
        val name2 = "Name 2"
        val name3 = "Name 3"

        val userAnswers = emptyUserAnswers
          .set(CharityNamePage(0), name1).success.value
          .set(AmountDiscretionYesNoPage(0), true).success.value
          .set(AddressYesNoPage(0), false).success.value
          .set(CharityBeneficiaryStatus(0), Completed).success.value

          .set(CharityNamePage(1), name2).success.value
          .set(AmountDiscretionYesNoPage(1), true).success.value
          .set(AddressYesNoPage(1), false).success.value
          .set(CharityBeneficiaryStatus(1), Completed).success.value

          .set(CharityNamePage(2), name3).success.value
          .set(CharityBeneficiaryStatus(2), InProgress).success.value

          .set(CharityBeneficiaryStatus(3), InProgress).success.value

        helper(userAnswers).rows mustEqual AddToRows(
          inProgress = List(
            AddRow(
              name = name3,
              typeLabel = label,
              changeUrl = controllers.register.beneficiaries.charityOrTrust.charity.routes.CharityAnswersController.onPageLoad(2, draftId).url,
              removeUrl = featureUnavailableUrl
            ),
            AddRow(
              name = default,
              typeLabel = label,
              changeUrl = controllers.register.beneficiaries.charityOrTrust.charity.routes.CharityAnswersController.onPageLoad(3, draftId).url,
              removeUrl = featureUnavailableUrl
            )
          ),
          complete = List(
            AddRow(
              name = name1,
              typeLabel = label,
              changeUrl = controllers.register.beneficiaries.charityOrTrust.charity.routes.CharityAnswersController.onPageLoad(0, draftId).url,
              removeUrl = featureUnavailableUrl
            ),
            AddRow(
              name = name2,
              typeLabel = label,
              changeUrl = controllers.register.beneficiaries.charityOrTrust.charity.routes.CharityAnswersController.onPageLoad(1, draftId).url,
              removeUrl = featureUnavailableUrl
            )
          )
        )
      }
    }
  }

}
