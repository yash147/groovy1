folder('Groovy') {
    displayName('Groovy')
    description('Folder for groovy')
}

folder('Groovy/Task-6') {
	displayName('Task-6')
    description('Folder containing all Task-4 jobs')
	
	job("Groovy/Task-6/job1")
	{
		description ("job 1")
		
		scm {
				github ('yash14/groovy1','master')
			}
		configure { it / 'triggers' / 'com.cloudbees.jenkins.GitHubPushTrigger' / 'spec' }
		steps{
		shell('sudo cp -rvf * /root/groovy/')
			}
	}
	
	job("Groovy/Task-6/job2")

	{

		description ("job 2")

		steps{

				shell('''
					if sudo ls /root/groovy/ | grep .html
					then
					 if sudo kubectl get pvc | grep pvc
					 then
						echo "mydeploy pvc already created"
					 else
						sudo kubectl create -f /root/groovy/mydeploy-pvc.yaml 
					 fi
					 if sudo kubectl get deploy | grep mydeploy
					 then
					   echo "mydeploy running perfectly"
					 else
					   sudo kubectl create -f /root/groovy/mydeploy.yaml 
					 fi
					else
					 if sudo kubectl get pvc | grep pvc
					 then
						echo "mydeploy pvc already created"
					 else
						sudo kubectl create -f /root/groovy/mydeploy-pvc.yaml 
					 fi
					 if sudo kubectl get deploy | grep mydeploy
					 then
					   echo "myhtmldeploy running perfectly"
					 else
					   sudo kubectl create -f /root/groovy/mydeploy.yaml 
					 fi
					fi
					sleep 60
					podpath=$(sudo kubectl get -o jsonpath="{.spec.ports[0].nodePort}" services mydeploy)
					podname=$(sudo kubectl get pod -l app=mydeploy -o jsonpath="{.items[0].metadata.name}" )
					sudo kubectl cp /root/groovy/index.html  $podname:/var/www/html/
					echo "goto http://192.168.99.105:$podpath"
				''')
		
			}
			 triggers {

				upstream('job1', 'SUCCESS')

			}
	}
	job("Groovy/Task-6/job3")
		{
			description ("job 3")
			steps{
			shell('''
				 status=$(curl -o /dev/null -sw "%{http_code}" http://192.168.99.105:32000/index.html)
				if [[$status == 200 ]]
				then
				exit 1
				else
				echo "wroking great"
				fi 
			 ''')
				}
				
			publishers {
			   mailer('yashbarange14@gmail.com', true, true)
		   }
			triggers {
				   upstream('job2', 'SUCCESS')
				}
		}


		}