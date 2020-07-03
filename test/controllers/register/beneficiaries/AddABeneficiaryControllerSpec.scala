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

package controllers.register.beneficiaries

import base.SpecBase
import forms.{AddABeneficiaryFormProvider, YesNoFormProvider}
import models.NormalMode
import models.core.pages.FullName
import models.registration.pages.AddABeneficiary
import models.registration.pages.Status.Completed
import pages.entitystatus.{ClassBeneficiaryStatus, IndividualBeneficiaryStatus}
import pages.register.beneficiaries.individual.IndividualBeneficiaryNamePage
import pages.register.beneficiaries.{AddABeneficiaryPage, ClassBeneficiaryDescriptionPage}
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import viewmodels.AddRow
import views.html.register.beneficiaries.{AddABeneficiaryView, AddABeneficiaryYesNoView}

class AddABeneficiaryControllerSpec extends SpecBase {

  def onwardRoute: Call = Call("GET", "/foo")

  def removeIndividualRoute(index : Int): String =
    routes.RemoveIndividualBeneficiaryController.onPageLoad(index, fakeDraftId).url

  def removeClassRoute(index : Int): String =
    routes.RemoveClassOfBeneficiaryController.onPageLoad(index, fakeDraftId).url

  lazy val addABeneficiaryRoute = routes.AddABeneficiaryController.onPageLoad(fakeDraftId).url

  lazy val addOnePostRoute = routes.AddABeneficiaryController.submitOne(fakeDraftId).url

  lazy val addAnotherPostRoute = routes.AddABeneficiaryController.submitAnother(fakeDraftId).url

  val formProvider = new AddABeneficiaryFormProvider()
  val form = formProvider()

  val yesNoForm = new YesNoFormProvider().withPrefix("addABeneficiaryYesNo")

  lazy val beneficiariesComplete = List(
    AddRow("First Last", typeLabel = "Individual Beneficiary", "/trusts-registration/feature-not-available", removeIndividualRoute(0)),
    AddRow("description", typeLabel = "Class of beneficiaries", "/trusts-registration/feature-not-available", removeClassRoute(0))
  )

  val userAnswersWithBeneficiariesComplete = emptyUserAnswers
    .set(IndividualBeneficiaryNamePage(0), FullName("First", None, "Last")).success.value
    .set(IndividualBeneficiaryStatus(0), Completed).success.value
    .set(ClassBeneficiaryDescriptionPage(0), "description").success.value
    .set(ClassBeneficiaryStatus(0), Completed).success.value

  "AddABeneficiary Controller" when {

    "no data" must {

      "redirect to Session Expired for a GET if no existing data is found" in {

        val application = applicationBuilder(userAnswers = None).build()

        val request = FakeRequest(GET, addABeneficiaryRoute)

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER
        redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url

        application.stop()
      }

      "redirect to Session Expired for a POST if no existing data is found" in {

        val application = applicationBuilder(userAnswers = None).build()

        val request =
          FakeRequest(POST, addAnotherPostRoute)
            .withFormUrlEncodedBody(("value", AddABeneficiary.values.head.toString))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url

        application.stop()
      }
    }

    "there are no beneficiaries" must {

      "return OK and the correct view for a GET" in {

        val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

        val request = FakeRequest(GET, addABeneficiaryRoute)

        val result = route(application, request).value

        val view = application.injector.instanceOf[AddABeneficiaryYesNoView]

        status(result) mustEqual OK

        contentAsString(result) mustEqual
          view(form, NormalMode, fakeDraftId)(fakeRequest, messages).toString

        application.stop()
      }

      "redirect to the next page when valid data is submitted" in {

        val application =
          applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

        val request =
          FakeRequest(POST, addOnePostRoute)
            .withFormUrlEncodedBody(("value", "true"))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual onwardRoute.url

        application.stop()
      }

      "return a Bad Request and errors when invalid data is submitted" in {

        val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

        val request =
          FakeRequest(POST, addOnePostRoute)
            .withFormUrlEncodedBody(("value", ""))

        val boundForm = yesNoForm.bind(Map("value" -> ""))

        val view = application.injector.instanceOf[AddABeneficiaryYesNoView]

        val result = route(application, request).value

        status(result) mustEqual BAD_REQUEST

        contentAsString(result) mustEqual
          view(boundForm, NormalMode, fakeDraftId)(fakeRequest, messages).toString

        application.stop()
      }

    }

    "there are beneficiaries" must {

      "return OK and the correct view for a GET" in {

        val application = applicationBuilder(userAnswers = Some(userAnswersWithBeneficiariesComplete)).build()

        val request = FakeRequest(GET, addABeneficiaryRoute)

        val result = route(application, request).value

        val view = application.injector.instanceOf[AddABeneficiaryView]

        status(result) mustEqual OK

        contentAsString(result) mustEqual
          view(form, NormalMode, fakeDraftId, Nil, beneficiariesComplete, "You have added 2 beneficiaries")(fakeRequest, messages).toString

        application.stop()
      }

      "populate the view without value on a GET when the question has previously been answered" in {
        val userAnswers = userAnswersWithBeneficiariesComplete.
          set(AddABeneficiaryPage,AddABeneficiary.YesNow).success.value

        val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

        val request = FakeRequest(GET, addABeneficiaryRoute)

        val result = route(application, request).value

        val view = application.injector.instanceOf[AddABeneficiaryView]

        status(result) mustEqual OK

        contentAsString(result) mustEqual
          view(form, NormalMode, fakeDraftId, Nil, beneficiariesComplete, "You have added 2 beneficiaries")(fakeRequest, messages).toString

        application.stop()
      }

      "redirect to the next page when valid data is submitted" in {

        val application =
          applicationBuilder(userAnswers = Some(userAnswersWithBeneficiariesComplete)).build()

        val request =
          FakeRequest(POST, addAnotherPostRoute)
            .withFormUrlEncodedBody(("value", AddABeneficiary.options.head.value))

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).value mustEqual fakeNavigator.desiredRoute.url

        application.stop()
      }

      "return a Bad Request and errors when invalid data is submitted" in {

        val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

        val request =
          FakeRequest(POST, addAnotherPostRoute)
            .withFormUrlEncodedBody(("value", "invalid value"))

        val boundForm = form.bind(Map("value" -> "invalid value"))

        val view = application.injector.instanceOf[AddABeneficiaryView]

        val result = route(application, request).value

        status(result) mustEqual BAD_REQUEST

        contentAsString(result) mustEqual
          view(boundForm, NormalMode, fakeDraftId, Nil, Nil, "Add a beneficiary")(fakeRequest, messages).toString

        application.stop()
      }

    }

  }
}
