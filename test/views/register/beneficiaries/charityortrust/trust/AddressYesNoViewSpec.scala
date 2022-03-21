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

package views.register.beneficiaries.charityortrust.trust

import forms.YesNoFormProvider
import pages.register.beneficiaries.charityortrust.trust.NamePage
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.YesNoViewBehaviours
import views.html.register.beneficiaries.charityortrust.trust.AddressYesNoView

class AddressYesNoViewSpec extends YesNoViewBehaviours {

  val messageKeyPrefix = "trustBeneficiaryAddressYesNo"
  val index = 0
  val name = "First Last"

  val form = new YesNoFormProvider().withPrefix(messageKeyPrefix)

  "TrustBeneficiaryAddressYesNo view" must {

    val userAnswers = emptyUserAnswers
      .set(NamePage(index), name).success.value

    val view = viewFor[AddressYesNoView](Some(userAnswers))

    def applyView(form: Form[_]): HtmlFormat.Appendable =
      view.apply(form, fakeDraftId, name, index)(fakeRequest, messages)

    behave like dynamicTitlePage(applyView(form), messageKeyPrefix, name)

    behave like pageWithBackLink(applyView(form))

    behave like yesNoPage(form, applyView, messageKeyPrefix,  None, Seq(name))

    behave like pageWithASubmitButton(applyView(form))
  }
}
