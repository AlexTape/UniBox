var app = {
	debugMode : true,
	url : '/UniBox',
	lastTimeStamp : 0,
	cometSource : '/Communicator/JavaScript',
	dataSource : '/Database',
	authSource : '/Auth',
	adminSource : '/Admin',
	listening : true,
	initialize : function() {
		app.bindEventHandles();
		app.login();
		app.listen();
		app.initGameTable();
		app.initRankingTable();
		app.updateGameTable();
		app.updateRankingTable();
		app.gotActiveGame();
		app.updateFormulars();
	},
	updateFormulars : function() {
		var categorySelection = $("#gameTypeInput");
		jQuery.ajax({
			type : "GET",
			url : app.url + app.dataSource,
			data : "action=getCategories",
			success : function(data) {
				$.each(data, function(index, value) {
					$("#gameTypeInput").append(
							'<option value="' + value.CatID + '">'
									+ value.GameTitle + '</option>');
				});
			},
			error : function(e) {
				app
						.newsticker("warning",
								"Could not update game categories..!");
			},
			async : false
		});
	},
	listen : function() {
		$('#comet-frame').on("readystatechange", function() {
			console.log("ARGH");
			console.log(this.readyState);
		});
		$('#comet-frame').attr("src", app.url + app.cometSource + '?1').on(
				"load",
				function() {
					var timeDiff = Math.floor(Date.now() / 1000)
							- app.lastTimeStamp;
					console.log("reconnect listener after timeout.. Listened "
							+ timeDiff + " seconds..");
					app.reconnectDialog();
				}).on("error", function(error) {
			app.reconnectDialog();
		});
	},
	reconnectDialog : function() {
		swal({
			title : "Disconnected..",
			text : "You are disconnected from the server.. (Timeout)",
			type : "warning",
			showCancelButton : false,
			confirmButtonColor : "#DD6B55",
			confirmButtonText : "Try reconnect!",
			closeOnConfirm : false,
		}, function(isConfirm) {
			if (isConfirm) {
				app.reload();
			} else {
				app.redirect("timeout");
			}
		});
	},
	activateMenu : function(target) {
		$("#triggerDashboard").removeClass("btnActive");
		$("#triggerChat").removeClass("btnActive");
		$("#triggerAdmin").removeClass("btnActive");
		switch (target.id) {
		case "triggerDashboard":
			app.hideChat();
			app.hideAdmin();
			$(target).addClass("btnActive");
			break;
		case "triggerChat":
			app.showChat();
			app.hideAdmin();
			$(target).addClass("btnActive");
			break;
		case "triggerAdmin":
			app.showAdmin();
			app.hideChat();
			$(target).addClass("btnActive");
			break;
		default:
			break;
		}
		if (window.innerWidth < 768) {
			$(".navbar-toggle").click();
		}
	},
	hideChat : function() {
		$(".messengerFrame").addClass("out");
		setTimeout(function() {
			$(".messengerFrame").addClass("hidden");
		}, 200);
		$("html").removeClass("no-scroll");
	},
	showChat : function() {
		$(".messengerFrame").removeClass("out");
		$(".messengerFrame").removeClass("hidden");
		$("html").addClass("no-scroll");
		setTimeout(function() {
			$("#messengerInputForm").focus();
		}, 100);
	},
	hideAdmin : function() {
		$(".adminFrame").addClass("out");
		setTimeout(function() {
			$(".adminFrame").addClass("hidden");
		}, 200);
	},
	showAdmin : function() {
		$(".adminFrame").removeClass("out");
		$(".adminFrame").removeClass("hidden");
	},
	bindEventHandles : function() {
		$('#messengerInputForm').keypress(function(e) {
			if (e.which == 13) {
				var formContent = $('#messengerInputForm');
				app.post(formContent.val());
				formContent.val("");
				e.preventDefault();
			}
		});
		$('#refreshOption').click(function() {
			app.reload();
		});
		$('#triggerDashboard').click(function(e) {
			app.activateMenu(this);
		});
		$('#triggerChat').click(function() {
			app.activateMenu(this);
		});
		$('#triggerAdmin').click(function() {
			app.activateMenu(this);
		});
		$('#createGameForm').on(
				'submit',
				function(e) {
					e.preventDefault();
					e.stopPropagation();
					var gameName = $('#gameNameInput').val();
					var gameType = $('#gameTypeInput').val();
					var gameDescription = $('#gameDescriptionInput').val();
					jQuery
							.ajax({
								type : "POST",
								url : app.url + app.dataSource,
								data : "action=createGame&gameName=" + gameName
										+ "&catId=" + gameType + "&descr="
										+ gameDescription,
								success : function(result) {
									app.newsticker("success",
											"Game created successful.");
									swal("Good job!", "Game created!",
											"success");
									$('#createGameForm').trigger("reset");
									app.updateGameTable();
								},
								error : function(e) {
									app.newsticker("warning",
											"Could not create game..!");
									swal("Ups..", "Could not create game..",
											"warning");
									$('#createGameForm').trigger("reset");
								},
								async : false
							});
				});
		$('#changePasswordForm')
				.on(
						'submit',
						function(e) {
							e.preventDefault();
							e.stopPropagation();
							var oldPassword = $('#oldPassword').val();
							var inputPassword = $('#inputPassword').val();
							var inputPasswordConfirm = $(
									'#inputPasswordConfirm').val();
							jQuery
									.ajax({
										type : "POST",
										url : app.url + app.authSource,
										data : "action=changePassword&oldPassword="
												+ Base64.encode(oldPassword)
												+ "&inputPassword="
												+ Base64.encode(inputPassword)
												+ "&inputPasswordConfirm="
												+ Base64
														.encode(inputPasswordConfirm),
										success : function(result) {
											$('#changePasswordModal').modal(
													'hide')
											if (result == "success") {
												app.newsticker("success",
														"Password changed.");

												swal("Good job!",
														"Password changed!",
														"success");
											} else {
												console.log(result);
												app
														.newsticker("warning",
																"Could not change password..!");
												swal(
														"Ups..",
														"Could not change password.. Wrong user password?",
														"warning");
												$('#changePasswordForm')
														.trigger("reset");
											}
										},
										error : function(e) {
											app
													.newsticker("warning",
															"Could not change password..!");
										},
										async : false
									});
						});
		$('#adminControlForm').on('submit', function(e) {
			e.preventDefault();
			e.stopPropagation();
		});
		$('#createUserBtn')
				.on(
						'click',
						function(e) {
							var name = $("#createUserField").val();
							if (name != "") {
								var isAdmin = 0;
								if ($("#isAdminCheckbox").prop('checked')) {
									isAdmin = 1;
								}
								jQuery
										.ajax({
											type : "POST",
											url : app.url + app.adminSource,
											data : "action=createUser&name="
													+ Base64.encode(name)
													+ "&adminRights=" + isAdmin,
											success : function(data) {
												console.log(data);
												swal("Good Job!",
														"User created..!",
														"success");
												$('#adminControlForm').trigger(
														"reset");
												$("#createUserField").focus();
											},
											error : function(e) {
												console.log(e);
												swal("Ups..",
														"Could not create user "
																+ name + "..!",
														"warning");
												$('#adminControlForm').trigger(
														"reset");
												$("#createUserField").focus();
											},
											async : false
										});
							} else {
								swal("Ups..", "Please type a name..", "warning");
								$('#createUserField').focus();
							}
						});
		$('#deleteUserBtn').on(
				'click',
				function(e) {

					var result = {
						"success" : [],
						"failed" : []
					};
					var json = {
						"data" : JSON.stringify($("#multiSelectDeleteUser")
								.val())
					};
					if (json["data"] != "null") {
						$("#multiSelectDeleteUser").val().forEach(function(id) {
							jQuery.ajax({
								type : "GET",
								url : app.url + app.adminSource,
								data : "action=deleteUser&userId=" + id,
								success : function(data) {
									result["success"].push(id);
									$('#adminControlForm').trigger("reset");
								},
								error : function(e) {
									console.log(e);
									result["failed"].push({
										"id" : id,
										"error" : e
									});
									$('#adminControlForm').trigger("reset");
								},
								async : false
							});
						});
						if (result["success"] == "") {
							result["success"] = "none";
						}
						if (result["failed"] == "") {
							result["failed"] = "none";
						}
						swal("Good Job!", "IDs of deletetd users: "
								+ result["success"]
								+ ", IDs of not deleted users: "
								+ result["failed"], "info");

						app.updateUsersSelection();
					} else {
						swal("Ups..", "No user(s) selected..", "warning");
					}
				});
		$('#deleteGameBtn').on(
				'click',
				function(e) {
					var result = {
						"success" : [],
						"failed" : []
					};
					var json = {
						"data" : JSON.stringify($("#multiSelectDeleteGame")
								.val())
					};
					if (json["data"] != "null") {
						$("#multiSelectDeleteGame").val().forEach(function(id) {
							jQuery.ajax({
								type : "GET",
								url : app.url + app.adminSource,
								data : "action=deleteGame&gameId=" + id,
								success : function(data) {
									result["success"].push(id);
									$('#adminControlForm').trigger("reset");
								},
								error : function(e) {
									console.log(e);
									result["failed"].push({
										"id" : id,
										"error" : e
									});
									$('#adminControlForm').trigger("reset");
								},
								async : false
							});
						});
						if (result["success"] == "") {
							result["success"] = "none";
						}
						if (result["failed"] == "") {
							result["failed"] = "none";
						}
						swal("Good Job!", "IDs of deleted games: "
								+ result["success"]
								+ ", IDs of not deleted games: "
								+ result["failed"], "info");

						app.updateGameTable();
					} else {
						swal("Ups..", "No game(s) selected..", "warning");
					}
				});
		$('#resetScoresBtn').on('click', function(e) {
			jQuery.ajax({
				type : "GET",
				url : app.url + app.adminSource,
				data : "action=resetScores",
				success : function(data) {
					swal("Good job!", "Scores reset!", "success");
				},
				error : function(e) {
					swal("Ups..", "Could not reset Scores..", "warning");
				},
				async : false
			});
		});
		$('#resetDbBtn').on(
				'click',
				function(e) {
					jQuery.ajax({
						type : "GET",
						url : app.url + app.adminSource,
						data : "action=resetDatabase",
						success : function(data) {
							// ARGH
							swal("Good job!", "Default database initialized!",
									"success");
						},
						error : function(e) {
							swal("Ups..", "Could not reset Database..",
									"warning");
						},
						async : false
					});
				});
		$('#newGameModal').on('shown.bs.modal', function() {
			$('#gameNameInput').focus();
		});
		$('#changePasswordModal').on('shown.bs.modal', function() {
			$('#oldPassword').focus();
		});
		$("#changePasswordForm").validator();
		$(document).on("keydown", function disableF5(e) {
			if ((e.which || e.keyCode) == 116) {
				if (!app.debugMode) {
					e.preventDefault();
					app.reload();
				}
			}
		});
	},
	reload : function() {
		swal({
			title : "Reloading..",
			text : "Please wait a second..",
			showConfirmButton : false,
			type : "success",
			timer : "2000"
		});
		app.updateGameTable();
		app.updateRankingTable();
		app.listen();
	},
	login : function() {
		jQuery.ajax({
			type : "POST",
			url : app.url + app.cometSource,
			data : "action=connect",
			success : function(result) {
				app.lastTimeStamp = Math.floor(Date.now() / 1000);
			},
			error : function() {
				app.redirect("login");
			},
			async : false
		});
	},
	logout : function() {
		window.location.replace("/UniBox/Auth?handle=logout");
	},
	post : function(message) {
		if (message) {
			jQuery
					.ajax({
						type : "POST",
						url : app.url + app.cometSource,
						data : "action=push&message=" + Base64.encode(message),
						success : function() {
							// tiny dirty hack for mobile devices in order of
							// the missing onResum() method to prevent offline
							// states in the chat overlay
							if (/Android|webOS|iPhone|iPad|iPod|BlackBerry|IEMobile|Opera Mini/i
									.test(navigator.userAgent)) {
								app.listening = false;
								setTimeout(function() {
									if (!app.listening) {
										app.reconnectDialog();
									}
								}, 1000);
							}
						},
						error : function() {
							app.redirect("post");
						},
						async : false
					});
		}
	},
	message : function(data) {
		$(
				'<div class="messengerElement"><div><span class="messageText"><span>'
						+ data.name + ':</span> ' + Base64.decode(data.message)
						+ '</span></div></div>').appendTo('#messengerContent')
				.fadeIn('slow');
		scrollBottom($('.messengerContent'));
	},
	notice : function(text) {
		$(
				'<div class="messengerElement"><div><span class="messageText"><span>System:</span> '
						+ text + '</span></div></div>').appendTo(
				'#messengerContent').fadeIn('slow');
		scrollBottom($('.messengerContainer'));
	},
	redirect : function(reason) {
		window.location.replace("/UniBox/?error=" + reason);
	},
	initGameTable : function() {
		var gameTable = $('#gamePanel').dataTable({
			"pagingType" : "simple",
			"iDisplayLength" : 10,
			"bLengthChange" : false,
			"responsive" : true,
			"bFilter" : true,
			"language" : {
				"processing" : "just a second..",
				"lengthMenu" : "Show _MENU_",
				"zeroRecords" : "No data available",
				"info" : "_END_ of _TOTAL_",
				"infoEmpty" : "0 of 0",
				"infoFiltered" : "out of _MAX_",
				"infoPostFix" : "",
			},
		});
		$('#gameFilter').keyup(function() {
			$("#gamePanel_filter input").val($(this).val());
			$("#gamePanel_filter input").keyup()
		});
	},
	updateGameTable : function() {
		var gameDataTable = $('#gamePanel').DataTable();
		jQuery.ajax({
			type : "GET",
			url : app.url + app.dataSource + "?action=getGames",
			success : function(data) {
				gameDataTable.clear().draw();
				$("#multiSelectDeleteGame").empty();
				for ( var i in data) {
					var gameJoinLink = '<a id="game-' + data[i].GameID
							+ '" href="javascript:app.joinGame('
							+ data[i].GameID + ')">Join</a>';
					gameDataTable.row.add([ gameJoinLink, data[i].GameID,
							data[i].GameTitle, data[i].GameName,
							data[i].NumberOfPlayers, data[i].Players ]);
					// update admin panel too

					$("#multiSelectDeleteGame").append(
							"<option value='" + data[i].GameID + "'>"
									+ data[i].GameName + "</option>");

				}
				gameDataTable.draw();
				app.gotActiveGame();
				// update admin panel too
				$('#multiSelectDeleteGame').multiselect({
					includeSelectAllOption : true,
					enableFiltering : true
				});
				$('#multiSelectDeleteGame').multiselect('rebuild');
			},
			error : function() {
				app.newsticker("warning", "Could not update game table..!");
			},
			statusCode : {
				403 : function() {
					app.redirect("forbidden");
				},
				0 : function() {
					app.redirect("server_offline");
				}
			},
			async : false
		});
	},
	initRankingTable : function() {
		var rankingTable = $('#rankingPanel').dataTable({
			"pagingType" : "simple",
			"iDisplayLength" : 10,
			"bLengthChange" : false,
			"responsive" : true,
			"bFilter" : true,
			"language" : {
				"processing" : "just a second..",
				"lengthMenu" : "Show _MENU_",
				"zeroRecords" : "No data available",
				"info" : "_END_ of _TOTAL_",
				"infoEmpty" : "0 of 0",
				"infoFiltered" : "out of _MAX_",
				"infoPostFix" : "",
			},
		});
		$('#rankingFilter').keyup(function() {
			$("#rankingPanel_filter input").val($(this).val());
			$("#rankingPanel_filter input").keyup()
		});
	},
	updateRankingTable : function() {
		// if ranking table should be updated, user table should be updated too
		app.updateUsersSelection();
		var rankingDataTable = $('#rankingPanel').DataTable();
		jQuery.ajax({
			type : "GET",
			url : app.url + app.dataSource + "?action=getRanking",
			success : function(data) {
				rankingDataTable.clear().draw();
				for ( var i in data) {
					rankingDataTable.row.add([ data[i].Rank, data[i].Name,
							data[i].Score ]);
				}
				rankingDataTable.draw();
			},
			error : function() {
				app.newsticker("warning", "Could not update ranking table..!");
			},
			statusCode : {
				403 : function() {
					app.redirect("forbidden");
				},
				0 : function() {
					app.redirect("server_offline");
				}
			},
			async : false
		});
	},
	updateUsersSelection : function() {
		$("#multiSelectDeleteUser").empty();
		jQuery.ajax({
			type : "GET",
			url : app.url + app.dataSource + "?action=getUsers",
			success : function(data) {
				for ( var i in data) {
					$("#multiSelectDeleteUser").append(
							"<option value='" + data[i].PlayerID + "'>"
									+ data[i].Name + "</option>");
				}
				$('#multiSelectDeleteUser').multiselect({
					includeSelectAllOption : false,
					enableFiltering : true
				});
				$('#multiSelectDeleteUser').multiselect('rebuild');
			},
			error : function() {
				app.newsticker("warning", "Could not update users data..!");
			},
			statusCode : {
				403 : function() {
					app.redirect("forbidden");
				},
				0 : function() {
					app.redirect("server_offline");
				}
			},
			async : false
		});
	},
	newsticker : function(type, message) {
		switch (type) {
		case "info":
			app.promoteNews(type, message);
			break;
		case "warning":
			app.promoteNews(type, message);
			break;
		case "success":
			app.promoteNews(type, message);
			break;
		case "danger":
			app.promoteNews(type, message);
			break;
		default:
			break;
		}
	},
	promoteNews : function(type, message) {
		$(".newsTicker").empty();
		$(".newsTicker").append(
				'<div class="alert alert-' + type + '" role="alert">' + message
						+ '</div>');
	},
	joinGame : function(id) {
		jQuery.ajax({
			type : "GET",
			url : app.url + "/Game?action=joinGame&gameId=" + id,
			success : function(data) {
				app.updateGameTable();
				var linkElement = $("#game-" + id);
				linkElement
						.attr("href", "javascript:app.leaveGame(" + id + ")");
				linkElement.text("Leave");
			},
			error : function() {
				app.newsticker("warning", "Could not rejoin game..!");
			},
			statusCode : {
				403 : function() {
					swal("Ups..", "Could not join that game..",
					"warning");
				},
				0 : function() {
					app.redirect("server_offline");
				}
			},
			async : false
		});
	},
	leaveGame : function(id) {
		jQuery
				.ajax({
					type : "GET",
					url : app.url + "/Game?action=leaveGame&gameId=" + id,
					success : function(data) {
						app.updateGameTable();
						var linkElement = $("#game-" + id);
						linkElement.attr("href", "javascript:app.joinGame("
								+ id + ")");
						linkElement.text("Join");
					},
					error : function() {
						app.newsticker("warning", "Could not releave game..!");
					},
					statusCode : {
						403 : function() {
							swal("Ups..", "Could not leave that game..",
							"warning");
						},
						0 : function() {
							app.redirect("server_offline");
						}
					},
					async : false
				});
	},
	gotActiveGame : function() {
		jQuery.ajax({
			type : "GET",
			url : app.url + "/Game?action=whichGame",
			success : function(data) {
				var id = data.replace("gameId:", "");
				var linkElement = $("#game-" + id);
				linkElement
						.attr("href", "javascript:app.leaveGame(" + id + ")");
				linkElement.text("Leave");
			},
			statusCode : {
				403 : function() {
					swal("Ups..", "Could not determine active game..",
					"warning");
				},
				0 : function() {
					app.redirect("server_offline");
				}
			},
			async : false
		});
	}
};
