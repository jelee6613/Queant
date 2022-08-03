from apscheduler.schedulers.background import BackgroundScheduler

from .save_to_db import save_db
from .save_to_db import save_bank_db

def start():
    scheduler = BackgroundScheduler()
    scheduler.add_job(save_db, 'interval', seconds = 30)
    scheduler.add_job(save_bank_db, 'interval', seconds = 30)
    #scheduler.add_job(save_db, trigger='cron', day_of_week='wed', hour=10, minute=50)
    #scheduler.add_job(save_bank_db, trigger='cron', day_of_week='mon', hour=10, minute=50)
    print("start!")
    scheduler.start()