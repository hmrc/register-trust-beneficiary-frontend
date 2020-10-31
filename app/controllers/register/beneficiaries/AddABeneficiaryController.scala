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

import config.FrontendAppConfig
import controllers.actions.register.{DraftIdRetrievalActionProvider, RegistrationDataRequiredAction, RegistrationIdentifierAction}
import forms.{AddABeneficiaryFormProvider, YesNoFormProvider}
import javax.inject.Inject
import models.Status.InProgress
import models.registration.pages.AddABeneficiary.NoComplete
import models.registration.pages.KindOfTrust.Employees
import models.{Enumerable, ReadOnlyUserAnswers, UserAnswers}
import navigation.Navigator
import pages.entitystatus.IndividualBeneficiaryStatus
import pages.register.KindOfTrustPage
import pages.register.beneficiaries.individual.RoleInCompanyPage
import pages.register.beneficiaries.{AddABeneficiaryPage, AddABeneficiaryYesNoPage}
import play.api.Logging
import play.api.data.Form
import play.api.i18n.{I18nSupport, Messages, MessagesApi, MessagesProvider}
import play.api.mvc.{Action, AnyContent, Call, MessagesControllerComponents}
import repositories.RegistrationsRepository
import sections.beneficiaries.IndividualBeneficiaries
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import utils.AddABeneficiaryViewHelper
import views.html.register.beneficiaries.{AddABeneficiaryView, AddABeneficiaryYesNoView, MaxedOutBeneficiariesView}

import scala.concurrent.{ExecutionContext, Future}

class AddABeneficiaryController @Inject()(
                                           override val messagesApi: MessagesApi,
                                           registrationsRepository: RegistrationsRepository,
                                           navigator: Navigator,
                                           identify: RegistrationIdentifierAction,
                                           getData: DraftIdRetrievalActionProvider,
                                           requireData: RegistrationDataRequiredAction,
                                           addAnotherFormProvider: AddABeneficiaryFormProvider,
                                           yesNoFormProvider: YesNoFormProvider,
                                           val controllerComponents: MessagesControllerComponents,
                                           addAnotherView: AddABeneficiaryView,
                                           yesNoView: AddABeneficiaryYesNoView,
                                           maxedOutView: MaxedOutBeneficiariesView,
                                           config: FrontendAppConfig
                                         )(implicit ec: ExecutionContext) extends FrontendBaseController with Logging

  with I18nSupport with Enumerable.Implicits with AnyBeneficiaries {

  private val addAnotherForm = addAnotherFormProvider()

  private val yesNoForm = yesNoFormProvider.withPrefix("addABeneficiaryYesNo")

  private def routes(draftId: String) =
    identify andThen getData(draftId) andThen requireData

  private def heading(count: Int)(implicit mp : MessagesProvider) = {
    count match {
      case 0 => Messages("addABeneficiary.heading")
      case 1 => Messages("addABeneficiary.singular.heading")
      case size => Messages("addABeneficiary.count.heading", size)
    }
  }

  def onPageLoad(draftId: String): Action[AnyContent] = routes(draftId).async {
    implicit request =>

      def updateUserAnswers(initialAnswers: UserAnswers): Future[UserAnswers] = {

        def setIndividualBeneficiaryStatuses(readOnly: Option[ReadOnlyUserAnswers]): UserAnswers = {
          readOnly match {
            case Some(settlorsAnswers) =>
              val isTrustForEmployeesOfCompany = settlorsAnswers.get(KindOfTrustPage).contains(Employees)
              initialAnswers.get(IndividualBeneficiaries).toList.flatten.zipWithIndex
                .foldLeft(initialAnswers)((ua, x) => {
                  val index = x._2
                  if (ua.get(RoleInCompanyPage(index)).isEmpty && isTrustForEmployeesOfCompany) {
                    ua.set(IndividualBeneficiaryStatus(index), InProgress).getOrElse(ua)
                  } else {
                    ua
                  }
                })
            case _ =>
              initialAnswers
          }
        }

        for {
          settlorsAnswers <- registrationsRepository.getSettlorsAnswers(draftId)
          updatedAnswers = setIndividualBeneficiaryStatuses(settlorsAnswers)
          _ <- registrationsRepository.set(updatedAnswers)
        } yield updatedAnswers
      }

      updateUserAnswers(request.userAnswers) map { userAnswers =>
        val rows = new AddABeneficiaryViewHelper(userAnswers, draftId).rows

        val allBeneficiaries = beneficiaries(userAnswers)

        if (allBeneficiaries.nonMaxedOutOptions.isEmpty) {
          logger.info(s"[AddABeneficiaryController][Session ID: ${request.sessionId}] ${request.internalId} has maxed out beneficiaries")
          Ok(maxedOutView(draftId, rows.inProgress, rows.complete, heading(rows.count)))
        } else {
          if(rows.count > 0) {
            logger.info(s"[AddABeneficiaryController][Session ID: ${request.sessionId}] ${request.internalId} has not maxed out beneficiaries")
            val listOfMaxed = allBeneficiaries.maxedOutOptions.map(_.messageKey)
            Ok(addAnotherView(addAnotherForm, draftId, rows.inProgress, rows.complete, heading(rows.count), listOfMaxed))
          } else {
            logger.info(s"[AddABeneficiaryController][Session ID: ${request.sessionId}] ${request.internalId} has added no beneficiaries")
            Ok(yesNoView(yesNoForm, draftId))
          }
        }
      }
  }

  def submitOne(draftId : String) : Action[AnyContent] = routes(draftId).async {
    implicit request =>
      yesNoForm.bindFromRequest().fold(
        (formWithErrors: Form[_]) => {
          Future.successful(
            BadRequest(yesNoView(formWithErrors, draftId))
          )
        },
        value => {
          for {
            updatedAnswers <- Future.fromTry(request.userAnswers.set(AddABeneficiaryYesNoPage, value))
            _              <- registrationsRepository.set(updatedAnswers)
          } yield Redirect(navigator.nextPage(AddABeneficiaryYesNoPage, draftId, updatedAnswers))
        }
      )
  }

  def submitAnother(draftId: String): Action[AnyContent] = routes(draftId).async {
    implicit request =>

      addAnotherForm.bindFromRequest().fold(
        (formWithErrors: Form[_]) => {
          val rows = new AddABeneficiaryViewHelper(request.userAnswers, draftId).rows

          val allBeneficiaries = beneficiaries(request.userAnswers)
          val listOfMaxed = allBeneficiaries.maxedOutOptions.map(_.messageKey)

          Future.successful(BadRequest(addAnotherView(formWithErrors, draftId, rows.inProgress, rows.complete, heading(rows.count), listOfMaxed)))
        },
        value => {
          for {
            updatedAnswers <- Future.fromTry(request.userAnswers.set(AddABeneficiaryPage, value))
            _              <- registrationsRepository.set(updatedAnswers)
          } yield Redirect(navigator.nextPage(AddABeneficiaryPage, draftId, updatedAnswers))
        }
      )
  }

  def submitComplete(draftId: String): Action[AnyContent] = routes(draftId).async {
    implicit request =>
      for {
        updatedAnswers <- Future.fromTry(request.userAnswers.set(AddABeneficiaryPage, NoComplete))
        _              <- registrationsRepository.set(updatedAnswers)
      } yield Redirect(Call("GET", config.registrationProgressUrl(draftId)))
  }

}
